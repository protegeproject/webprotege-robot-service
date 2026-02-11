package edu.stanford.protege.robot.pipeline;

import edu.stanford.protege.webprotege.common.ProjectId;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record RobotPipeline(
        @Nonnull ProjectId projectId,
        @Nonnull PipelineId pipelineId,
        @Nullable String label,
        @Nullable String description,
        @Nonnull List<RobotPipelineStage> stages) {

    public RobotPipeline {
        Objects.requireNonNull(projectId, "projectId should not be null");
        Objects.requireNonNull(pipelineId, "pipelineId should not be null");
        Objects.requireNonNull(stages, "stages should not be null");
    }
}
