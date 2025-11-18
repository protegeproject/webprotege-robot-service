package edu.stanford.protege.robot.service;

import com.google.common.collect.Maps;
import edu.stanford.protege.robot.pipeline.*;
import edu.stanford.protege.robot.service.exception.RobotServiceException;
import edu.stanford.protege.robot.service.storer.MinioDocumentStorer;
import edu.stanford.protege.webprotege.common.BlobLocation;
import edu.stanford.protege.webprotege.common.ProjectId;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Provider;
import org.obolibrary.robot.CommandState;
import org.obolibrary.robot.IOHelper;
import org.springframework.stereotype.Service;

/**
 * Spring service for executing chains of ROBOT commands sequentially, threading state between
 * commands.
 *
 * <p>
 * This executor enables programmatic composition of ROBOT command pipelines. Each command
 * receives the output from the previous command via CommandState, creating processing pipelines
 * without intermediate file I/O.
 */
@Service
public class RobotPipelineExecutor {

  private final Provider<CommandState> commandStateProvider;
  private final IOHelper ioHelper;
  private final MinioDocumentStorer minioDocumentStorer;
  private final PipelineLogger pipelineLogger;

  /**
   * Creates a new RobotPipelineExecutor with Spring-injected dependencies.
   *
   * @param commandStateProvider
   *          provider for CommandState instances
   * @param ontologyStorer
   *          helper for ontology I/O operations
   * @param pipelineLogger
   *          logger for tracking pipeline execution progress and events
   */
  public RobotPipelineExecutor(
      @Nonnull Provider<CommandState> commandStateProvider,
      @Nonnull IOHelper ontologyStorer,
      @Nonnull MinioDocumentStorer minioDocumentStorer,
      @Nonnull PipelineLogger pipelineLogger) {
    this.commandStateProvider = commandStateProvider;
    this.ioHelper = ontologyStorer;
    this.minioDocumentStorer = minioDocumentStorer;
    this.pipelineLogger = pipelineLogger;
  }

  /**
   * Executes a chain of ROBOT commands sequentially.
   *
   * <p>
   * Commands receive the output from the previous command automatically. The first command receives
   * the loaded input ontology, and subsequent commands receive the modified ontology from their
   * predecessor.
   *
   * @param projectId
   *          the unique project identifier
   * @param inputOntologyPath
   *          path to the input ontology file (required)
   * @param revisionNumber
   *          the revision number of the input ontology
   * @param pipeline
   *          the ROBOT pipeline containing the sequence of commands to execute
   * @return the final CommandState after all commands have executed
   * @throws RobotServiceException
   *           if any command fails or I/O error occurs
   */
  public PipelineExecutionId executePipeline(ProjectId projectId, Path inputOntologyPath, long revisionNumber,
      RobotPipeline pipeline) throws RobotServiceException {

    // Validate inputs
    Objects.requireNonNull(projectId, "projectId cannot be null");
    Objects.requireNonNull(inputOntologyPath, "inputOntologyPath cannot be null");
    Objects.requireNonNull(pipeline, "pipeline cannot be null");

    var pipelineId = pipeline.pipelineId();
    var executionId = PipelineExecutionId.generate();

    var startTimestamp = Instant.now();
    try {
      pipelineLogger.pipelineExecutionStarted(projectId, executionId, pipelineId);

      // Get fresh CommandState
      var state = commandStateProvider.get();

      // Load input ontology
      try {
        pipelineLogger.loadingOntologyStarted(projectId, executionId, pipelineId);
        var ontology = ioHelper.loadOntology(inputOntologyPath.toFile());
        pipelineLogger.loadingOntologySucceeded(projectId, executionId, pipelineId);
        state.setOntology(ontology);
        state.setOntologyPath(inputOntologyPath.toString());
      } catch (Throwable t) {
        pipelineLogger.loadingOntologyFailed(projectId, executionId, pipelineId, t);
        throw new RobotServiceException("Error loading ontology: " + t.getMessage(), t);
      }

      // The map between output relative path to the blob location
      var outputFiles = Maps.<RelativePath, BlobLocation>newHashMap();

      // Execute each command sequentially, threading state between them
      for (var pipelineStage : pipeline.stages()) {
        var robotCommand = pipelineStage.command();
        var command = robotCommand.getCommand();
        var args = robotCommand.getArgsArray();
        try {
          pipelineLogger.pipelineStageRunStarted(projectId, executionId, pipelineId, pipelineStage);
          state = command.execute(state, args);
          pipelineLogger.pipelineStageRunFinished(projectId, executionId, pipelineId, pipelineStage);

          if (pipelineStage.producedOutput()) {
            try {
              var targetOutputPath = pipelineStage.outputPath();
              var ontologyPath = targetOutputPath.asString();
              pipelineLogger.savingOntologyStarted(projectId, executionId, pipelineId, ontologyPath);
              ioHelper.saveOntology(state.getOntology(), ontologyPath);
              var blobLocation = minioDocumentStorer.storeDocument(ontologyPath);
              outputFiles.put(targetOutputPath, blobLocation);
              pipelineLogger.savingOntologySucceeded(projectId, executionId, pipelineId);
            } catch (Throwable t) {
              pipelineLogger.savingOntologyFailed(projectId, executionId, pipelineId, t);
              throw new RobotServiceException("Error saving ontology: " + t.getMessage(), t);
            }
          }
        } catch (Throwable t) {
          pipelineLogger.pipelineStageFailed(projectId, executionId, pipelineId, pipelineStage, t);
          throw new RobotServiceException("Error executing pipeline stage: " + t.getMessage(), t);
        }
      }

      var endTimestamp = Instant.now();

      // Produce the PipelineStatus, and store it to MongoDB
      // Produce the PipelineOutput, and store it to MongoDB
      var pipelineOutput = PipelineSuccessResult.create(projectId, revisionNumber, pipeline, startTimestamp,
          endTimestamp, outputFiles);

      return executionId;

    } finally {
      pipelineLogger.pipelineExecutionFinished(projectId, executionId, pipelineId);
    }
  }
}
