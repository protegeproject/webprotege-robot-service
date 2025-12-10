package edu.stanford.protege.robot.pipeline;

import com.google.common.collect.ImmutableList;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents the status of a pipeline execution, including the status of each individual stage.
 *
 * <p>
 * This is an immutable record that tracks the execution state of a pipeline. Each time a stage
 * status changes, a new PipelineStatus instance is created with the updated stage information.
 */
public record PipelineStatus(
    @Nonnull PipelineExecutionId executionId,
    @Nonnull Instant startTime,
    @Nullable Instant endTime,
    @Nonnull List<PipelineStageStatus> stages,
    @Nonnull RobotPipeline pipeline) {

  public PipelineStatus {
    Objects.requireNonNull(executionId, "executionId cannot be null");
    Objects.requireNonNull(startTime, "startTime cannot be null");
    Objects.requireNonNull(stages, "stages cannot be null");
    Objects.requireNonNull(pipeline, "pipeline cannot be null");

    // Make stages list immutable
    stages = List.copyOf(stages);

    // Validation: endTime cannot be before startTime
    if (endTime != null && endTime.isBefore(startTime)) {
      throw new IllegalArgumentException("endTime cannot be before startTime");
    }
  }

  /**
   * Creates an initial PipelineStatus with all stages in WAITING state.
   */
  public static PipelineStatus create(
      @Nonnull PipelineExecutionId executionId,
      @Nonnull PipelineId pipelineId,
      @Nonnull Instant startTime,
      @Nonnull RobotPipeline pipeline) {
    var stageStatuses = pipeline.stages().stream()
        .map(stage -> PipelineStageStatus.waiting(stage.stageId(), stage.outputPath()))
        .toList();
    return new PipelineStatus(executionId, startTime, null, stageStatuses, pipeline);
  }

  /**
   * Creates a new PipelineStatus with the specified stage marked as RUNNING.
   */
  public static PipelineStatus withStageRunning(
      @Nonnull PipelineStatus previousStatus,
      @Nonnull PipelineStageId stageId) {
    var stages = previousStatus.stages();
    var updatedStages = replaceStageStatus(stages, stageId, PipelineStageStatus::running);
    return new PipelineStatus(
        previousStatus.executionId,
        previousStatus.startTime,
        previousStatus.endTime,
        updatedStages,
        previousStatus.pipeline);
  }

  /**
   * Creates a new PipelineStatus with the specified stage marked as FINISHED_WITH_SUCCESS.
   */
  public static PipelineStatus withStageSuccess(
      @Nonnull PipelineStatus previousStatus,
      @Nonnull PipelineStageId stageId) {
    var stages = previousStatus.stages();
    var updatedStages = replaceStageStatus(stages, stageId, PipelineStageStatus::finishedWithSuccess);
    return new PipelineStatus(
        previousStatus.executionId,
        previousStatus.startTime,
        previousStatus.endTime,
        updatedStages,
        previousStatus.pipeline);
  }

  /**
   * Creates a new PipelineStatus with the specified stage marked as FINISHED_WITH_ERROR.
   */
  public static PipelineStatus withStageError(
      @Nonnull PipelineStatus previousStatus,
      @Nonnull PipelineStageId stageId) {
    var stages = previousStatus.stages();
    var updatedStages = replaceStageStatus(stages, stageId, PipelineStageStatus::finishedWithError);
    return new PipelineStatus(
        previousStatus.executionId,
        previousStatus.startTime,
        previousStatus.endTime,
        updatedStages,
        previousStatus.pipeline);
  }

  /**
   * Creates a new PipelineStatus with the end time set.
   */
  public static PipelineStatus withEndTime(
      @Nonnull PipelineStatus previousStatus,
      @Nonnull Instant endTime) {
    return new PipelineStatus(
        previousStatus.executionId,
        previousStatus.startTime,
        endTime,
        previousStatus.stages,
        previousStatus.pipeline);
  }

  /**
   * Helper method that replaces a specific stage with a new PipelineStageStatus object.
   */
  private static ImmutableList<PipelineStageStatus> replaceStageStatus(
      List<PipelineStageStatus> stages,
      PipelineStageId stageId,
      StageStatusFactory factory) {
    return stages.stream()
        .map(stage -> stage.stageId().equals(stageId)
            ? factory.create(stage.stageId(), stage.outputFile())
            : stage)
        .collect(ImmutableList.toImmutableList());
  }

  /**
   * Checks if the pipeline is currently running (has at least one stage running or waiting).
   */
  public boolean isRunning() {
    return stages.stream().anyMatch(stage -> stage.isRunning() || stage.isWaiting());
  }

  /**
   * Checks if the pipeline finished successfully (all stages finished successfully).
   */
  public boolean isSuccessful() {
    return !stages.isEmpty() && stages.stream().allMatch(PipelineStageStatus::isSuccessful);
  }

  /**
   * Checks if the pipeline finished with at least one error.
   */
  public boolean isFailed() {
    return stages.stream().anyMatch(PipelineStageStatus::isFailed);
  }

  /**
   * Functional interface for creating a new PipelineStageStatus with a specific status.
   */
  @FunctionalInterface
  private interface StageStatusFactory {
    PipelineStageStatus create(PipelineStageId stageId, RelativePath outputFile);
  }
}
