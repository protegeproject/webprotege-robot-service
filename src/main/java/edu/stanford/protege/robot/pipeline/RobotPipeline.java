package edu.stanford.protege.robot.pipeline;

import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.ProjectId;
import java.util.List;

@JsonTypeName("RobotPipeline")
public record RobotPipeline(
    ProjectId projectId,
    PipelineId pipelineId,
    String label,
    String description,
    List<RobotPipelineStage> stages) {
}
