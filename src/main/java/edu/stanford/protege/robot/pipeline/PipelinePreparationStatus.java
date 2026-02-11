package edu.stanford.protege.robot.pipeline;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record PipelinePreparationStatus(
        @Nonnull StageStatus status,
        @Nullable String message) {

    public PipelinePreparationStatus {
        Objects.requireNonNull(status, "status cannot be null");
    }

    public static PipelinePreparationStatus waiting(@Nullable String message) {
        return new PipelinePreparationStatus(StageStatus.WAITING, message);
    }

    public static PipelinePreparationStatus running(@Nullable String message) {
        return new PipelinePreparationStatus(StageStatus.RUNNING, message);
    }

    public static PipelinePreparationStatus finishedWithSuccess(@Nullable String message) {
        return new PipelinePreparationStatus(StageStatus.FINISHED_WITH_SUCCESS, message);
    }

    public static PipelinePreparationStatus finishedWithError(@Nullable String message) {
        return new PipelinePreparationStatus(StageStatus.FINISHED_WITH_ERROR, message);
    }

    public boolean isWaiting() {
        return status == StageStatus.WAITING;
    }

    public boolean isRunning() {
        return status == StageStatus.RUNNING;
    }

    public boolean isSuccessful() {
        return status == StageStatus.FINISHED_WITH_SUCCESS;
    }

    public boolean isFailed() {
        return status == StageStatus.FINISHED_WITH_ERROR;
    }
}
