package edu.stanford.protege.robot.pipeline;

import java.time.Instant;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents the status of a pipeline execution.
 */
public record PipelineStatus(
    @Nonnull PipelineExecutionId pipelineExecutionId,
    @Nonnull PipelineId pipelineId,
    @Nonnull Instant startTimestamp,
    @Nullable Instant endTimestamp,
    @Nonnull Status status) {

  public PipelineStatus {
    Objects.requireNonNull(pipelineExecutionId, "pipelineExecutionId cannot be null");
    Objects.requireNonNull(pipelineId, "pipelineId cannot be null");
    Objects.requireNonNull(startTimestamp, "startTimestamp cannot be null");
    Objects.requireNonNull(status, "status cannot be null");

    // Validation: endTimestamp should be null only when status is RUNNING
    if (status == Status.RUNNING && endTimestamp != null) {
      throw new IllegalArgumentException("endTimestamp must be null when status is RUNNING");
    }
    if (status != Status.RUNNING && endTimestamp == null) {
      throw new IllegalArgumentException("endTimestamp must be set when status is not RUNNING");
    }
    if (endTimestamp != null && endTimestamp.isBefore(startTimestamp)) {
      throw new IllegalArgumentException("endTimestamp cannot be before startTimestamp");
    }
  }

  /**
   * Creates a PipelineStatus for a running pipeline.
   */
  public static PipelineStatus running(
      @Nonnull PipelineExecutionId pipelineExecutionId,
      @Nonnull PipelineId pipelineId,
      @Nonnull Instant startTimestamp) {
    return new PipelineStatus(pipelineExecutionId, pipelineId, startTimestamp, null, Status.RUNNING);
  }

  /**
   * Creates a PipelineStatus for a successfully finished pipeline.
   */
  public static PipelineStatus finishedWithSuccess(
      @Nonnull PipelineExecutionId pipelineExecutionId,
      @Nonnull PipelineId pipelineId,
      @Nonnull Instant startTimestamp,
      @Nonnull Instant endTimestamp) {
    return new PipelineStatus(pipelineExecutionId, pipelineId, startTimestamp, endTimestamp,
        Status.FINISHED_WITH_SUCCESS);
  }

  /**
   * Creates a PipelineStatus for a failed pipeline.
   */
  public static PipelineStatus finishedWithError(
      @Nonnull PipelineExecutionId pipelineExecutionId,
      @Nonnull PipelineId pipelineId,
      @Nonnull Instant startTimestamp,
      @Nonnull Instant endTimestamp) {
    return new PipelineStatus(pipelineExecutionId, pipelineId, startTimestamp, endTimestamp,
        Status.FINISHED_WITH_ERROR);
  }

  /**
   * Checks if the pipeline is currently running.
   *
   * @return true if status is RUNNING
   */
  public boolean isRunning() {
    return status == Status.RUNNING;
  }

  /**
   * Checks if the pipeline finished successfully.
   *
   * @return true if status is FINISHED_WITH_SUCCESS
   */
  public boolean isSuccessful() {
    return status == Status.FINISHED_WITH_SUCCESS;
  }

  /**
   * Checks if the pipeline finished with failures.
   *
   * @return true if status is FINISHED_WITH_ERROR
   */
  public boolean isFailed() {
    return status == Status.FINISHED_WITH_ERROR;
  }

  /**
   * Represents the execution status of a pipeline.
   */
  public enum Status {
    /**
     * The pipeline is currently running.
     */
    RUNNING,

    /**
     * The pipeline finished successfully without errors.
     */
    FINISHED_WITH_SUCCESS,

    /**
     * The pipeline finished with one or more errors.
     */
    FINISHED_WITH_ERROR
  }
}
