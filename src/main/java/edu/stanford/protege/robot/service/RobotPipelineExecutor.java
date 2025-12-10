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
import org.obolibrary.robot.CommandState;
import org.obolibrary.robot.IOHelper;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger logger = LoggerFactory.getLogger(RobotPipelineExecutor.class);

  private final Provider<CommandState> commandStateProvider;
  private final IOHelper ioHelper;
  private final MinioDocumentStorer minioDocumentStorer;
  private final PipelineStatusRepository pipelineStatusRepository;
  private final PipelineSuccessResultRepository successResultRepository;
  private final PipelineLogger pipelineLogger;

  public RobotPipelineExecutor(
      @Nonnull Provider<CommandState> commandStateProvider,
      @Nonnull IOHelper ontologyStorer,
      @Nonnull MinioDocumentStorer minioDocumentStorer,
      @Nonnull PipelineStatusRepository pipelineStatusRepository,
      @Nonnull PipelineSuccessResultRepository successResultRepository,
      @Nonnull PipelineLogger pipelineLogger) {
    this.commandStateProvider = commandStateProvider;
    this.ioHelper = ontologyStorer;
    this.minioDocumentStorer = minioDocumentStorer;
    this.pipelineStatusRepository = pipelineStatusRepository;
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
   */
  public void executePipeline(@Nonnull ProjectId projectId, @Nonnull PipelineExecutionId executionId,
      @Nonnull Path inputOntologyPath, long revisionNumber, @Nonnull RobotPipeline pipeline) {

    // Validate inputs
    Objects.requireNonNull(executionId, "executionId cannot be null");
    Objects.requireNonNull(projectId, "projectId cannot be null");
    Objects.requireNonNull(inputOntologyPath, "inputOntologyPath cannot be null");
    Objects.requireNonNull(pipeline, "pipeline cannot be null");

    var pipelineId = pipeline.pipelineId();

    // Start time
    var startTimestamp = Instant.now();

    try {
      // Create the initial pipeline status
      var status = PipelineStatus.create(executionId, pipelineId, startTimestamp, pipeline);
      safeSaveStatus(pipelineId, status);
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
      var outputFileMap = Maps.<RelativePath, BlobLocation>newHashMap();

      // Execute each command sequentially, threading state between them
      for (var pipelineStage : pipeline.stages()) {
        var stageId = pipelineStage.stageId();
        var robotCommand = pipelineStage.command();
        var command = robotCommand.getCommand();
        var args = robotCommand.getArgsArray();
        try {
          // Update status that a pipeline stage is running
          status = PipelineStatus.withStageRunning(status, stageId);
          safeSaveStatus(pipelineId, status);
          pipelineLogger.pipelineStageStarted(projectId, executionId, pipelineId, command);

          // Update the state
          state = command.execute(state, args);

          // Check if the pipeline stage produces an output
          if (pipelineStage.producedOutput()) {
            var outputLocation = pipelineStage.outputPath();
            var ontology = state.getOntology();
            try {
              var blobLocation = saveOntologyOutput(projectId, executionId, pipelineId, ontology, outputLocation);
              outputFileMap.put(outputLocation, blobLocation);
            } catch (Throwable t) {
              pipelineLogger.savingOntologyFailed(projectId, executionId, pipelineId, t);
              throw new RobotServiceException("Pipeline stage failed due to I/O Exception: " + t.getMessage(), t);
            }
          }
          // Update status that a pipeline stage is finished successfully
          status = PipelineStatus.withStageSuccess(status, stageId);
          safeSaveStatus(pipelineId, status);
          pipelineLogger.pipelineStageFinishedWithSuccess(projectId, executionId, pipelineId, command);
        } catch (Throwable t) {
          // Update status that the pipeline stage is finished but with errors
          status = PipelineStatus.withStageError(status, stageId);
          safeSaveStatus(pipelineId, status);
          pipelineLogger.pipelineStageFinishedWithError(projectId, executionId, pipelineId, pipelineStage, t);
          throw new RobotServiceException("Pipeline stage failed: " + t.getMessage(), t);
        }
      }
      // End time
      var endTimestamp = Instant.now();

      // Update status with the end timestamp
      status = PipelineStatus.withEndTime(status, endTimestamp);
      safeSaveStatus(pipelineId, status);

      // Report the success results and save it to MongoDB
      var result = PipelineSuccessResult.create(executionId, projectId, revisionNumber, pipeline, startTimestamp,
          endTimestamp, outputFileMap);
      safeSaveResult(pipelineId, result);

      pipelineLogger.pipelineExecutionFinishedWithSuccess(projectId, executionId, pipelineId);
    } catch (Throwable t) {
      pipelineLogger.pipelineExecutionFinishedWithError(projectId, executionId, pipelineId, t);
    }
  }

  private void safeSaveResult(PipelineId pipelineId, PipelineSuccessResult result) {
    try {
      successResultRepository.saveResult(result);
    } catch (Throwable t) {
      logger.error("{} Pipeline result failed to save in MongoDB", pipelineId, t);
    }
  }

  private void safeSaveStatus(PipelineId pipelineId, PipelineStatus status) {
    try {
      pipelineStatusRepository.saveStatus(status);
    } catch (Throwable t) {
      logger.error("{} Pipeline progress status failed to save in MongoDB", pipelineId, t);
    }
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
}
