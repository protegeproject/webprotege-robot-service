package edu.stanford.protege.robot.service;

import edu.stanford.protege.robot.pipeline.*;
import edu.stanford.protege.robot.service.snapshot.ProjectOntologySnapshotProvider;
import edu.stanford.protege.webprotege.common.ProjectId;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
/**
 * Orchestrates asynchronous pipeline execution by first creating a project ontology snapshot and
 * then delegating to the {@link RobotPipelineExecutor}.
 *
 * <p>
 * The orchestrator is responsible for preparation status updates and snapshot lifecycle events so
 * that clients can display progress before pipeline stages begin.
 */
public class RobotPipelineOrchestrator {

  private static final Logger logger = LoggerFactory.getLogger(RobotPipelineOrchestrator.class);

  private final RobotPipelineExecutor executor;

  private final ProjectOntologySnapshotProvider snapshotProvider;

  private final PipelineStatusRepository pipelineStatusRepository;

  private final PipelineLogger pipelineLogger;

  private final Executor pipelineExecutor;

  public RobotPipelineOrchestrator(RobotPipelineExecutor executor,
      ProjectOntologySnapshotProvider snapshotProvider,
      PipelineStatusRepository pipelineStatusRepository,
      PipelineLogger pipelineLogger,
      @Qualifier("robotPipelineExecutor") Executor pipelineExecutor) {
    this.executor = executor;
    this.snapshotProvider = snapshotProvider;
    this.pipelineStatusRepository = pipelineStatusRepository;
    this.pipelineLogger = pipelineLogger;
    this.pipelineExecutor = pipelineExecutor;
  }

  /**
   * Starts an asynchronous pipeline execution for the supplied project and pipeline.
   *
   * <p>
   * This method returns immediately after creating the execution id and persisting an initial
   * pipeline status. Snapshot creation and pipeline execution occur off-thread.
   *
   * @param projectId
   *          the project whose ontology will be snapshotted
   * @param pipeline
   *          the pipeline to execute
   * @return the generated pipeline execution id
   */
  public PipelineExecutionId executeAsync(@Nonnull ProjectId projectId, @Nonnull RobotPipeline pipeline) {
    var executionId = PipelineExecutionId.generate();
    // Seed pipeline status immediately so clients can render "preparing snapshot" before work starts.
    var status = PipelineStatus.createWithPreparationStatus(
        executionId,
        pipeline.pipelineId(),
        Instant.now(),
        pipeline,
        PipelinePreparationStatus.waiting("Preparing ontology snapshot"));
    pipelineStatusRepository.saveStatus(status);

    // Fire-and-forget: snapshot + pipeline execute off-thread to keep the handler non-blocking.
    CompletableFuture.runAsync(() -> executeAsyncInternal(projectId, executionId, pipeline), pipelineExecutor);
    return executionId;
  }

  /**
   * Runs snapshot creation and pipeline execution in the background for a given execution id.
   *
   * @param projectId
   *          the project whose ontology will be snapshotted
   * @param executionId
   *          the execution id for this run
   * @param pipeline
   *          the pipeline to execute
   */
  private void executeAsyncInternal(ProjectId projectId, PipelineExecutionId executionId, RobotPipeline pipeline) {
    try {
      // Snapshotting is an explicit preparation phase with its own events/status updates.
      updatePreparationStatus(executionId, pipeline, PipelinePreparationStatus.running("Preparing ontology snapshot"));
      pipelineLogger.snapshotOntologyStarted(projectId, executionId, pipeline.pipelineId());
      var snapshot = snapshotProvider.createSnapshot(projectId);
      var ontology = snapshot.ontology();
      var revisionNumber = snapshot.revisionNumber();
      updatePreparationStatus(executionId, pipeline,
          PipelinePreparationStatus.finishedWithSuccess("Ontology snapshot ready"));
      pipelineLogger.snapshotOntologySucceeded(projectId, executionId, pipeline.pipelineId());

      // Hand off to the executor once the ontology snapshot is ready.
      executor.executePipeline(projectId, executionId, ontology, revisionNumber, pipeline);
      logger.info("{} {} Pipeline execution finished successfully", projectId, executionId);
    } catch (Exception e) {
      logger.info("{} {} Pipeline execution failed: {}", projectId, executionId, e.getMessage());
      // If snapshotting fails, finalize status immediately (no pipeline stages will run).
      updatePreparationStatusWithEndTime(executionId, pipeline,
          PipelinePreparationStatus.finishedWithError("Snapshot failed: " + e.getMessage()));
      pipelineLogger.snapshotOntologyFailed(projectId, executionId, pipeline.pipelineId(), e);
      pipelineLogger.pipelineExecutionFinishedWithError(projectId, executionId, pipeline.pipelineId(), e);
    }
  }

  /**
   * Persists the preparation status for a pipeline execution, recreating the status if necessary.
   *
   * @param executionId
   *          the pipeline execution id
   * @param pipeline
   *          the pipeline definition
   * @param preparationStatus
   *          the new preparation status to save
   */
  private void updatePreparationStatus(PipelineExecutionId executionId, RobotPipeline pipeline,
      PipelinePreparationStatus preparationStatus) {
    // Defensive: recreate status if it was evicted or missing in storage.
    var status = pipelineStatusRepository.findStatus(executionId)
        .orElseGet(() -> PipelineStatus.createWithPreparationStatus(
            executionId,
            pipeline.pipelineId(),
            Instant.now(),
            pipeline,
            preparationStatus));
    var updated = PipelineStatus.withPreparationStatus(status, preparationStatus);
    pipelineStatusRepository.saveStatus(updated);
  }

  /**
   * Persists a preparation status update and marks the pipeline execution as ended.
   *
   * @param executionId
   *          the pipeline execution id
   * @param pipeline
   *          the pipeline definition
   * @param preparationStatus
   *          the terminal preparation status to save
   */
  private void updatePreparationStatusWithEndTime(PipelineExecutionId executionId, RobotPipeline pipeline,
      PipelinePreparationStatus preparationStatus) {
    // Terminal update for snapshot failures; ensures the pipeline isn't reported as running.
    var status = pipelineStatusRepository.findStatus(executionId)
        .orElseGet(() -> PipelineStatus.createWithPreparationStatus(
            executionId,
            pipeline.pipelineId(),
            Instant.now(),
            pipeline,
            preparationStatus));
    var updated = PipelineStatus.withPreparationStatus(status, preparationStatus);
    updated = PipelineStatus.withEndTime(updated, Instant.now());
    pipelineStatusRepository.saveStatus(updated);
  }
}
