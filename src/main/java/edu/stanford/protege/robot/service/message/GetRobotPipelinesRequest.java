package edu.stanford.protege.robot.service.message;

import static edu.stanford.protege.robot.service.message.GetRobotPipelinesRequest.CHANNEL;

import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.common.Request;
import java.util.Objects;
import javax.annotation.Nonnull;

@JsonTypeName(CHANNEL)
public record GetRobotPipelinesRequest(@Nonnull ProjectId projectId)
    implements
      Request<GetRobotPipelinesResponse> {

  public static final String CHANNEL = "webprotege.robot.GetRobotPipelines";

  public GetRobotPipelinesRequest {
    Objects.requireNonNull(projectId, "Project ID cannot be null");
  }

  @Override
  public String getChannel() {
    return CHANNEL;
  }
}
