package edu.stanford.protege.robot.pipeline.event;

import edu.stanford.protege.robot.pipeline.PipelineExecutionId;
import edu.stanford.protege.webprotege.common.ProjectEvent;

sealed interface SnapshotOntologyEvent
    extends
      ProjectEvent permits SnapshotOntologyStartedEvent, SnapshotOntologySucceededEvent, SnapshotOntologyFailedEvent {

  PipelineExecutionId executionId();
}
