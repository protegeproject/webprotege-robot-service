package edu.stanford.protege.robot;

import edu.stanford.protege.robot.pipeline.PipelineRepository;
import edu.stanford.protege.robot.service.message.GetRobotPipelinesRequest;
import edu.stanford.protege.robot.service.message.GetRobotPipelinesResponse;
import edu.stanford.protege.webprotege.ipc.CommandHandler;
import edu.stanford.protege.webprotege.ipc.ExecutionContext;
import edu.stanford.protege.webprotege.ipc.WebProtegeHandler;
import javax.annotation.Nonnull;
import reactor.core.publisher.Mono;

@WebProtegeHandler
public class GetRobotPipelinesHandler
    implements
      CommandHandler<GetRobotPipelinesRequest, GetRobotPipelinesResponse> {

  private final PipelineRepository pipelineRepository;

  public GetRobotPipelinesHandler(PipelineRepository pipelineRepository) {
    this.pipelineRepository = pipelineRepository;
  }

  @Nonnull
  @Override
  public String getChannelName() {
    return GetRobotPipelinesRequest.CHANNEL;
  }

  @Override
  public Class<GetRobotPipelinesRequest> getRequestClass() {
    return GetRobotPipelinesRequest.class;
  }

  @Override
  public Mono<GetRobotPipelinesResponse> handleRequest(GetRobotPipelinesRequest request,
      ExecutionContext executionContext) {
    var pipelines = pipelineRepository.findPipelines(request.projectId());
    return Mono.just(new GetRobotPipelinesResponse(pipelines));
  }
}
