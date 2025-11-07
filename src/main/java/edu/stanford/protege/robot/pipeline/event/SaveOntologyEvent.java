package edu.stanford.protege.robot.pipeline.event;

import edu.stanford.protege.robot.pipeline.PipelineExecutionId;
import edu.stanford.protege.webprotege.common.ProjectEvent;

sealed interface SaveOntologyEvent
    extends
      ProjectEvent permits SaveOntologyStartedEvent, SaveOntologySucceededEvent, SaveOntologyFailedEvent {

  PipelineExecutionId executionId();
}
