package edu.stanford.protege.robot;

import edu.stanford.protege.robot.pipeline.PipelineRepository;
import edu.stanford.protege.robot.pipeline.RobotPipeline;
import edu.stanford.protege.robot.service.message.SetRobotPipelinesRequest;
import edu.stanford.protege.robot.service.message.SetRobotPipelinesResponse;
import edu.stanford.protege.webprotege.ipc.CommandHandler;
import edu.stanford.protege.webprotege.ipc.ExecutionContext;
import edu.stanford.protege.webprotege.ipc.WebProtegeHandler;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import reactor.core.publisher.Mono;

@WebProtegeHandler
public class SetRobotPipelinesHandler
    implements
      CommandHandler<SetRobotPipelinesRequest, SetRobotPipelinesResponse> {

  private final PipelineRepository pipelineRepository;

  public SetRobotPipelinesHandler(PipelineRepository pipelineRepository) {
    this.pipelineRepository = pipelineRepository;
  }

  @Nonnull
  @Override
  public String getChannelName() {
    return SetRobotPipelinesRequest.CHANNEL;
  }

  @Override
  public Class<SetRobotPipelinesRequest> getRequestClass() {
    return SetRobotPipelinesRequest.class;
  }

  @Override
  public Mono<SetRobotPipelinesResponse> handleRequest(SetRobotPipelinesRequest request,
      ExecutionContext executionContext) {
    var projectId = request.projectId();
    var normalized = normalizeProjectId(projectId, request.pipelines());
    pipelineRepository.deletePipelines(projectId);
    pipelineRepository.savePipelines(normalized);
    var saved = pipelineRepository.findPipelines(projectId);
    return Mono.just(new SetRobotPipelinesResponse(saved));
  }

  private List<RobotPipeline> normalizeProjectId(@Nonnull edu.stanford.protege.webprotege.common.ProjectId projectId,
      @Nonnull List<RobotPipeline> pipelines) {
    return pipelines.stream()
        .map(pipeline -> new RobotPipeline(
            projectId,
            pipeline.pipelineId(),
            pipeline.label(),
            pipeline.description(),
            pipeline.stages()))
        .collect(Collectors.toList());
  }
}
