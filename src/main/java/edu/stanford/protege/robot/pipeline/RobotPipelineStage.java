package edu.stanford.protege.robot.pipeline;

import edu.stanford.protege.robot.command.RobotCommand;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record RobotPipelineStage(
    @Nonnull PipelineStageId stageId,
    @Nullable String label,
    @Nullable String description,
    @Nonnull RobotCommand command,
    @Nullable RelativePath outputPath) {

  public RobotPipelineStage {
    Objects.requireNonNull(stageId, "stageId must not be null");
    Objects.requireNonNull(command, "command must not be null");
  }

  public boolean producedOutput() {
    return outputPath != null;
  }
}
