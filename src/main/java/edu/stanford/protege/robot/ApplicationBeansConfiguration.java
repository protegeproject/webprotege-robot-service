package edu.stanford.protege.robot;

import edu.stanford.protege.robot.pipeline.PipelineLogger;
import edu.stanford.protege.robot.service.RobotPipelineOrchestrator;
import edu.stanford.protege.webprotege.ipc.EventDispatcher;
import edu.stanford.protege.webprotege.ipc.WebProtegeIpcApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(WebProtegeIpcApplication.class)
public class ApplicationBeansConfiguration {

  @Bean
  ExecuteRobotCommandsHandler createExecuteRobotCommandsHandler(RobotPipelineOrchestrator orchestrator) {
    return new ExecuteRobotCommandsHandler(orchestrator);
  }

  @Bean
  GetRobotPipelinesHandler createGetRobotPipelinesHandler(
      edu.stanford.protege.robot.pipeline.PipelineRepository pipelineRepository) {
    return new GetRobotPipelinesHandler(pipelineRepository);
  }

  @Bean
  SetRobotPipelinesHandler createSetRobotPipelinesHandler(
      edu.stanford.protege.robot.pipeline.PipelineRepository pipelineRepository) {
    return new SetRobotPipelinesHandler(pipelineRepository);
  }

  @Bean
  PipelineLogger pipelineLogger(EventDispatcher eventDispatcher) {
    return new PipelineLogger(eventDispatcher);
  }
}
