package edu.stanford.protege.robot.pipeline;

import edu.stanford.protege.robot.pipeline.event.*;
import edu.stanford.protege.webprotege.common.EventId;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.ipc.EventDispatcher;
import org.obolibrary.robot.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipelineLogger {

  private final Logger logger = LoggerFactory.getLogger(PipelineLogger.class);

  private final EventDispatcher eventDispatcher;

  public PipelineLogger(EventDispatcher eventDispatcher) {
    this.eventDispatcher = eventDispatcher;
  }

  public void pipelineExecutionStarted(ProjectId projectId, PipelineExecutionId executionId, PipelineId pipelineId) {
    logger.info("{} {} {} ROBOT pipeline execution started", projectId, executionId, pipelineId);
    eventDispatcher
        .dispatchEvent(new ExecutePipelineStartedEvent(projectId, executionId, pipelineId, EventId.generate()));
  }

  public void pipelineExecutionFinishedWithSuccess(ProjectId projectId, PipelineExecutionId executionId,
      PipelineId pipelineId) {
    logger.info("{} {} {} ROBOT pipeline execution finished with success", projectId, executionId, pipelineId);
    eventDispatcher
        .dispatchEvent(new ExecutePipelineFinishedEvent(projectId, executionId, pipelineId, EventId.generate()));
  }

  public void pipelineExecutionFinishedWithError(ProjectId projectId, PipelineExecutionId executionId,
      PipelineId pipelineId, Throwable t) {
    logger.info("{} {} {} ROBOT pipeline execution finished with error: {}", projectId, executionId, pipelineId,
        t.getMessage());
    eventDispatcher
        .dispatchEvent(
            new ExecutePipelineFailedEvent(projectId, executionId, pipelineId, EventId.generate(), t.getMessage()));
  }

  public void loadingOntologyStarted(ProjectId projectId, PipelineExecutionId executionId, PipelineId pipelineId) {
    logger.info("{} {} {} ROBOT pipeline loading ontology", projectId, executionId, pipelineId);
    eventDispatcher.dispatchEvent(new LoadOntologyStartedEvent(projectId, executionId, pipelineId, EventId.generate()));
  }

  public void loadingOntologySucceeded(ProjectId projectId, PipelineExecutionId executionId, PipelineId pipelineId) {
    logger.info("{} {} {} ROBOT pipeline loaded ontology", projectId, executionId, pipelineId);
    eventDispatcher
        .dispatchEvent(new LoadOntologySucceededEvent(projectId, executionId, pipelineId, EventId.generate()));
  }

  public void loadingOntologyFailed(ProjectId projectId, PipelineExecutionId executionId, PipelineId pipelineId,
      Throwable t) {
    logger.error("{} {} {} ROBOT pipeline loading ontology failed", projectId, executionId, pipelineId, t);
    eventDispatcher.dispatchEvent(
        new LoadOntologyFailedEvent(projectId, executionId, pipelineId, EventId.generate(), t.getMessage()));
  }

  public void pipelineStageStarted(ProjectId projectId, PipelineExecutionId executionId, PipelineId pipelineId,
      Command command) {
    logger.info("{} {} {} ROBOT pipeline stage started: {}", projectId, executionId, pipelineId, command.getName());
    eventDispatcher
        .dispatchEvent(new RunPipelineStageStartedEvent(projectId, executionId, pipelineId, EventId.generate()));
  }

  public void pipelineStageFinishedWithSuccess(ProjectId projectId, PipelineExecutionId executionId,
      PipelineId pipelineId,
      Command command) {
    logger.info("{} {} {} ROBOT pipeline stage finished: {}", projectId, executionId, pipelineId, command.getName());
    eventDispatcher
        .dispatchEvent(new RunPipelineStageFinishedEvent(projectId, executionId, pipelineId, EventId.generate()));
  }

  public void pipelineStageFinishedWithError(ProjectId projectId, PipelineExecutionId executionId,
      PipelineId pipelineId,
      RobotPipelineStage pipelineStage, Throwable t) {
    logger.error("{} {} {} ROBOT pipeline stage failed: {}", projectId, executionId, pipelineId, pipelineStage, t);
    eventDispatcher
        .dispatchEvent(
            new RunPipelineStageFailedEvent(projectId, executionId, pipelineId, EventId.generate(), t.getMessage()));
  }

  public void savingOntologyStarted(ProjectId projectId, PipelineExecutionId executionId, PipelineId pipelineId,
      String ontologyPath) {
    logger.info("{} {} {} ROBOT pipeline saving ontology: {}", projectId, executionId, pipelineId, ontologyPath);
    eventDispatcher.dispatchEvent(new SaveOntologyStartedEvent(projectId, executionId, pipelineId, EventId.generate()));
  }

  public void savingOntologySucceeded(ProjectId projectId, PipelineExecutionId executionId, PipelineId pipelineId) {
    logger.info("{} {} {} ROBOT pipeline saved ontology succeeded", projectId, executionId, pipelineId);
    eventDispatcher
        .dispatchEvent(new SaveOntologySucceededEvent(projectId, executionId, pipelineId, EventId.generate()));
  }

  public void savingOntologyFailed(ProjectId projectId, PipelineExecutionId executionId, PipelineId pipelineId,
      Throwable t) {
    logger.error("{} {} {} ROBOT pipeline saving ontology failed", projectId, executionId, pipelineId, t);
    eventDispatcher.dispatchEvent(
        new SaveOntologyFailedEvent(projectId, executionId, pipelineId, EventId.generate(), t.getMessage()));
  }
}
