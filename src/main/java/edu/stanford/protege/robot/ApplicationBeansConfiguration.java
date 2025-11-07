package edu.stanford.protege.robot;

import edu.stanford.protege.robot.pipeline.PipelineLogger;
import edu.stanford.protege.robot.service.RobotPipelineExecutor;
import edu.stanford.protege.webprotege.ipc.EventDispatcher;
import edu.stanford.protege.webprotege.ipc.WebProtegeIpcApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(WebProtegeIpcApplication.class)
public class ApplicationBeansConfiguration {

  @Bean
  ExecuteRobotCommandsHandler createExecuteRobotCommandsHandler(
      RobotPipelineExecutor executor,
      PipelineLogger pipelineLogger) {
    return new ExecuteRobotCommandsHandler(executor, pipelineLogger);
  }

  @Bean
  PipelineLogger pipelineLogger(EventDispatcher eventDispatcher) {
    return new PipelineLogger(eventDispatcher);
  }
}
