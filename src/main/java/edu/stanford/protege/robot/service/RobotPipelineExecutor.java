package edu.stanford.protege.robot.service;

import com.google.common.collect.Maps;
import edu.stanford.protege.robot.pipeline.*;
import edu.stanford.protege.robot.service.exception.RobotServiceException;
import edu.stanford.protege.robot.service.storer.MinioDocumentStorer;
import edu.stanford.protege.webprotege.common.BlobLocation;
import edu.stanford.protege.webprotege.common.ProjectId;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Provider;
import org.obolibrary.robot.Command;
import org.obolibrary.robot.CommandState;
import org.obolibrary.robot.IOHelper;
import org.semanticweb.owlapi.model.OWLOntology;
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
  private final PipelineSuccessResultRepository successResultRepository;
  private final PipelineLogger pipelineLogger;

  public RobotPipelineExecutor(
      @Nonnull Provider<CommandState> commandStateProvider,
      @Nonnull IOHelper ontologyStorer,
      @Nonnull MinioDocumentStorer minioDocumentStorer,
      @Nonnull PipelineSuccessResultRepository successResultRepository,
      @Nonnull PipelineLogger pipelineLogger) {
    this.commandStateProvider = commandStateProvider;
    this.ioHelper = ontologyStorer;
    this.minioDocumentStorer = minioDocumentStorer;
    this.successResultRepository = successResultRepository;
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
   * @param executionId
   *          the unique pipeline execution identifier
   * @param inputOntologyPath
   *          path to the input ontology file (required)
   * @param revisionNumber
   *          the revision number of the input ontology
   * @param pipeline
   *          the ROBOT pipeline containing the sequence of commands to execute
   * @throws RobotServiceException
   *           if any command fails or I/O error occurs
   */
  public void executePipeline(ProjectId projectId, PipelineExecutionId executionId, Path inputOntologyPath,
      long revisionNumber,
      RobotPipeline pipeline) throws RobotServiceException {

    // Validate inputs
    Objects.requireNonNull(projectId, "projectId cannot be null");
    Objects.requireNonNull(inputOntologyPath, "inputOntologyPath cannot be null");
    Objects.requireNonNull(pipeline, "pipeline cannot be null");

    var pipelineId = pipeline.pipelineId();

    // Start time
    var startTimestamp = Instant.now();
    try {
      pipelineLogger.pipelineExecutionStarted(projectId, executionId, pipelineId);

      // Get fresh CommandState
      var state = commandStateProvider.get();

      // Load input ontology
      try {
        var ontology = loadOntology(projectId, executionId, pipelineId, inputOntologyPath);
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
          state = runCommand(projectId, executionId, pipelineId, state, command, args);
          // Check if the pipeline stage produces an output
          if (pipelineStage.producedOutput()) {
            var outputLocation = pipelineStage.outputPath();
            var ontology = state.getOntology();
            try {
              var blobLocation = saveOntologyOutput(projectId, executionId, pipelineId, ontology, outputLocation);
              outputFiles.put(outputLocation, blobLocation);
            } catch (IOException e) {
              pipelineLogger.savingOntologyFailed(projectId, executionId, pipelineId, e);
              throw new RobotServiceException("Error saving ontology: " + e.getMessage(), e);
            }
          }
        } catch (Throwable t) {
          pipelineLogger.pipelineStageFailed(projectId, executionId, pipelineId, pipelineStage, t);
          throw new RobotServiceException("Error executing pipeline stage: " + t.getMessage(), t);
        }
      }
      // End time
      var endTimestamp = Instant.now();

      // Produce the PipelineStatus, and store it to MongoDB

      // Produce the PipelineOutput, and store it to MongoDB
      var result = PipelineSuccessResult.create(executionId, projectId, revisionNumber, pipeline, startTimestamp,
          endTimestamp, outputFiles);
      successResultRepository.saveResult(result);

    } finally {
      pipelineLogger.pipelineExecutionFinished(projectId, executionId, pipelineId);
    }
  }

  /**
   * Saves the ontology to the local filesystem and uploads it to MinIO storage.
   */
  private BlobLocation saveOntologyOutput(ProjectId projectId, PipelineExecutionId executionId, PipelineId pipelineId,
      OWLOntology ontology, RelativePath outputLocation) throws IOException {
    var ontologyPath = outputLocation.asString();
    pipelineLogger.savingOntologyStarted(projectId, executionId, pipelineId, ontologyPath);
    ioHelper.saveOntology(ontology, ontologyPath);
    var blobLocation = minioDocumentStorer.storeDocument(ontologyPath);
    pipelineLogger.savingOntologySucceeded(projectId, executionId, pipelineId);
    return blobLocation;
  }

  /**
   * Executes a single ROBOT command with the given state and arguments.
   */
  private CommandState runCommand(ProjectId projectId, PipelineExecutionId executionId, PipelineId pipelineId,
      CommandState state, Command command, String[] args) throws Exception {
    pipelineLogger.pipelineStageRunStarted(projectId, executionId, pipelineId, command);
    state = command.execute(state, args);
    pipelineLogger.pipelineStageRunFinished(projectId, executionId, pipelineId, command);
    return state;
  }

  /**
   * Loads an ontology from the specified file path.
   */
  private OWLOntology loadOntology(ProjectId projectId, PipelineExecutionId executionId, PipelineId pipelineId,
      Path inputOntologyPath) throws IOException {
    pipelineLogger.loadingOntologyStarted(projectId, executionId, pipelineId);
    var ontology = ioHelper.loadOntology(inputOntologyPath.toFile());
    pipelineLogger.loadingOntologySucceeded(projectId, executionId, pipelineId);
    return ontology;
  }
}
