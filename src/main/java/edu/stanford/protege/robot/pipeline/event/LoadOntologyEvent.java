package edu.stanford.protege.robot.pipeline.event;

import edu.stanford.protege.robot.pipeline.PipelineExecutionId;
import edu.stanford.protege.webprotege.common.ProjectEvent;

sealed interface LoadOntologyEvent
        extends
            ProjectEvent permits LoadOntologyStartedEvent, LoadOntologySucceededEvent, LoadOntologyFailedEvent {

    PipelineExecutionId executionId();
}
