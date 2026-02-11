package edu.stanford.protege.robot.service.message;

import static edu.stanford.protege.robot.service.message.SetRobotPipelinesRequest.CHANNEL;

import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.robot.pipeline.RobotPipeline;
import edu.stanford.protege.webprotege.common.Response;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

@JsonTypeName(CHANNEL)
public record SetRobotPipelinesResponse(@Nonnull List<RobotPipeline> pipelines)
        implements
            Response {

    public SetRobotPipelinesResponse {
        Objects.requireNonNull(pipelines, "pipelines cannot be null");
    }
}
