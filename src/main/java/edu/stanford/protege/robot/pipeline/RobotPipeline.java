package edu.stanford.protege.robot.pipeline;

import java.util.List;

public record RobotPipeline(
    PipelineId pipelineId,
    List<RobotPipelineStage> stages) {
}
