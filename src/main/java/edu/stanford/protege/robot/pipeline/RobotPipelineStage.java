package edu.stanford.protege.robot.pipeline;

import edu.stanford.protege.robot.command.RobotCommand;
import java.util.Objects;
import javax.annotation.Nonnull;

public record RobotPipelineStage(
    @Nonnull String label,
    @Nonnull String description,
    @Nonnull RobotCommand command) {

  public RobotPipelineStage {
    Objects.requireNonNull(label, "label should not be null");
    Objects.requireNonNull(description, "description should not be null");
    Objects.requireNonNull(command, "command should not be null");
  }
}
