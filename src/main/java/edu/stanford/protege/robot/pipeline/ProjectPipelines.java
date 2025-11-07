package edu.stanford.protege.robot.pipeline;

import edu.stanford.protege.webprotege.common.ProjectId;
import java.util.List;

public record ProjectPipelines(ProjectId projectId, List<RobotPipeline> pipelines) {
}
