package edu.stanford.protege.robot.service.config;

import java.util.concurrent.Executor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableConfigurationProperties(RobotPipelineExecutorProperties.class)
public class RobotPipelineExecutorConfiguration {

  @Bean(name = "robotPipelineTaskExecutor")
  Executor robotPipelineTaskExecutor(RobotPipelineExecutorProperties properties) {
    var executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(properties.getCorePoolSize());
    executor.setMaxPoolSize(properties.getMaxPoolSize());
    executor.setQueueCapacity(properties.getQueueCapacity());
    executor.setThreadNamePrefix(properties.getThreadNamePrefix());
    executor.setWaitForTasksToCompleteOnShutdown(properties.isWaitForTasksToCompleteOnShutdown());
    executor.setAwaitTerminationSeconds(properties.getAwaitTerminationSeconds());
    executor.initialize();
    return executor;
  }
}
