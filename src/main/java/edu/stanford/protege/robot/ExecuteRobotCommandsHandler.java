package edu.stanford.protege.robot;

import edu.stanford.protege.robot.service.RobotPipelineExecutor;
import edu.stanford.protege.robot.service.exception.RobotServiceRuntimeException;
import edu.stanford.protege.robot.service.message.ExecuteRobotCommandsRequest;
import edu.stanford.protege.robot.service.message.ExecuteRobotCommandsResponse;
import edu.stanford.protege.webprotege.ipc.CommandHandler;
import edu.stanford.protege.webprotege.ipc.ExecutionContext;
import edu.stanford.protege.webprotege.ipc.WebProtegeHandler;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

@WebProtegeHandler
public class ExecuteRobotCommandsHandler
    implements
      CommandHandler<ExecuteRobotCommandsRequest, ExecuteRobotCommandsResponse> {

  private static final Logger logger = LoggerFactory.getLogger(ExecuteRobotCommandsHandler.class);

  private final RobotPipelineExecutor executor;

  public ExecuteRobotCommandsHandler(RobotPipelineExecutor executor) {
    this.executor = executor;
  }

  @Nonnull
  @Override
  public String getChannelName() {
    return ExecuteRobotCommandsRequest.CHANNEL;
  }

  @Override
  public Class<ExecuteRobotCommandsRequest> getRequestClass() {
    return ExecuteRobotCommandsRequest.class;
  }

  @Override
  public Mono<ExecuteRobotCommandsResponse> handleRequest(ExecuteRobotCommandsRequest request,
      ExecutionContext executionContext) {

    var projectId = request.projectId();
    var inputPath = Path.of("foo"); // TODO: Get the input path from the project ID
    var outputPath = Path.of("bar"); // TODO: Get the output path from modifying the input path

    try {
      // Execute command chain
      executor.executePipeline(projectId, inputPath, outputPath, request.pipeline());

      // Return response with execution details
      return Mono.just(new ExecuteRobotCommandsResponse(projectId, outputPath));
    } catch (Exception e) {
      logger.info("{} Error executing command request: {}", projectId, e.getMessage(), e);
      throw new RobotServiceRuntimeException("Error executing command request: " + e.getMessage(), e);
    }
  }
}
