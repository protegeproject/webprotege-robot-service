package edu.stanford.protege.robot.pipeline;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents the status of a single stage within a pipeline execution.
 *
 * <p>
 * This is an immutable record. To change the status, create a new instance using the factory
 * methods.
 */
public record PipelineStageStatus(
    @Nonnull PipelineStageId stageId,
    @Nonnull StageStatus status,
    @Nullable RelativePath outputFile) {

  public PipelineStageStatus {
    Objects.requireNonNull(stageId, "stageId cannot be null");
    Objects.requireNonNull(status, "status cannot be null");
  }

  /**
   * Creates a PipelineStageStatus in WAITING state.
   */
  public static PipelineStageStatus waiting(
      @Nonnull PipelineStageId stageId,
      @Nullable RelativePath outputFile) {
    return new PipelineStageStatus(stageId, StageStatus.WAITING, outputFile);
  }

  /**
   * Creates a PipelineStageStatus in RUNNING state.
   */
  public static PipelineStageStatus running(
      @Nonnull PipelineStageId stageId,
      @Nullable RelativePath outputFile) {
    return new PipelineStageStatus(stageId, StageStatus.RUNNING, outputFile);
  }

  /**
   * Creates a PipelineStageStatus in FINISHED_WITH_SUCCESS state.
   */
  public static PipelineStageStatus finishedWithSuccess(
      @Nonnull PipelineStageId stageId,
      @Nullable RelativePath outputFile) {
    return new PipelineStageStatus(stageId, StageStatus.FINISHED_WITH_SUCCESS, outputFile);
  }

  /**
   * Creates a PipelineStageStatus in FINISHED_WITH_ERROR state.
   */
  public static PipelineStageStatus finishedWithError(
      @Nonnull PipelineStageId stageId,
      @Nullable RelativePath outputFile) {
    return new PipelineStageStatus(stageId, StageStatus.FINISHED_WITH_ERROR, outputFile);
  }

  /**
   * Checks if this stage is waiting to be executed.
   */
  public boolean isWaiting() {
    return status == StageStatus.WAITING;
  }

  /**
   * Checks if this stage is currently running.
   */
  public boolean isRunning() {
    return status == StageStatus.RUNNING;
  }

  /**
   * Checks if this stage finished successfully.
   */
  public boolean isSuccessful() {
    return status == StageStatus.FINISHED_WITH_SUCCESS;
  }

  /**
   * Checks if this stage finished with an error.
   */
  public boolean isFailed() {
    return status == StageStatus.FINISHED_WITH_ERROR;
  }
}
