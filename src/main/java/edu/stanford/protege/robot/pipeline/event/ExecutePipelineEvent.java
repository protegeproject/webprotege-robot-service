package edu.stanford.protege.robot.pipeline.event;

import edu.stanford.protege.webprotege.common.ProjectEvent;

sealed interface ExecutePipelineEvent
        extends
            ProjectEvent permits ExecutePipelineStartedEvent, ExecutePipelineFinishedEvent, ExecutePipelineFailedEvent {

}
