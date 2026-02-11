package edu.stanford.protege.robot.service.message;

import static edu.stanford.protege.robot.service.message.SetRobotPipelinesRequest.CHANNEL;

import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.robot.pipeline.RobotPipeline;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.common.Request;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

@JsonTypeName(CHANNEL)
public record SetRobotPipelinesRequest(@Nonnull ProjectId projectId,
        @Nonnull List<RobotPipeline> pipelines)
        implements
            Request<SetRobotPipelinesResponse> {

    public static final String CHANNEL = "webprotege.robot.SetRobotPipelines";

    public SetRobotPipelinesRequest {
        Objects.requireNonNull(projectId, "Project ID cannot be null");
        Objects.requireNonNull(pipelines, "pipelines cannot be null");
    }

    @Override
    public String getChannel() {
        return CHANNEL;
    }
}
