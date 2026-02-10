package edu.stanford.protege.robot.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "webprotege.robot.executor")
public class RobotPipelineExecutorProperties {

  private int corePoolSize = 2;
  private int maxPoolSize = 4;
  private int queueCapacity = 100;
  private String threadNamePrefix = "robot-pipeline-";
  private boolean waitForTasksToCompleteOnShutdown = true;
  private int awaitTerminationSeconds = 60;

  public int getCorePoolSize() {
    return corePoolSize;
  }

  public void setCorePoolSize(int corePoolSize) {
    this.corePoolSize = corePoolSize;
  }

  public int getMaxPoolSize() {
    return maxPoolSize;
  }

  public void setMaxPoolSize(int maxPoolSize) {
    this.maxPoolSize = maxPoolSize;
  }

  public int getQueueCapacity() {
    return queueCapacity;
  }

  public void setQueueCapacity(int queueCapacity) {
    this.queueCapacity = queueCapacity;
  }

  public String getThreadNamePrefix() {
    return threadNamePrefix;
  }

  public void setThreadNamePrefix(String threadNamePrefix) {
    this.threadNamePrefix = threadNamePrefix;
  }

  public boolean isWaitForTasksToCompleteOnShutdown() {
    return waitForTasksToCompleteOnShutdown;
  }

  public void setWaitForTasksToCompleteOnShutdown(boolean waitForTasksToCompleteOnShutdown) {
    this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
  }

  public int getAwaitTerminationSeconds() {
    return awaitTerminationSeconds;
  }

  public void setAwaitTerminationSeconds(int awaitTerminationSeconds) {
    this.awaitTerminationSeconds = awaitTerminationSeconds;
  }
}
