package edu.stanford.protege.robot.service.message;

import static edu.stanford.protege.robot.service.message.ExecuteRobotCommandsRequest.CHANNEL;

import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.robot.pipeline.RobotPipeline;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.common.Request;
import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Request message for executing a chain of ROBOT commands.
 *
 * <p>
 * This record represents the JSON request format for the ROBOT service, containing the input
 * ontology path, output path, and a list of commands to execute sequentially.
 *
 * @param projectId
 *          WebProtege unique project identifier
 * @param pipeline
 *          the ROBOT pipeline containing the sequence of commands to execute
 */
@JsonTypeName(CHANNEL)
public record ExecuteRobotCommandsRequest(
    @Nonnull ProjectId projectId,
    @Nonnull RobotPipeline pipeline) implements Request<ExecuteRobotCommandsResponse> {

  public static final String CHANNEL = "webprotege.robot.ExecuteRobotCommands";

  public ExecuteRobotCommandsRequest {
    Objects.requireNonNull(projectId, "Project ID cannot be null");
    Objects.requireNonNull(pipeline, "Pipeline cannot be null");
  }

  @Override
  public String getChannel() {
    return CHANNEL;
  }
}
