package edu.stanford.protege.robot.pipeline.event;

import edu.stanford.protege.robot.pipeline.PipelineExecutionId;
import edu.stanford.protege.webprotege.common.ProjectEvent;

sealed interface RunPipelineStageEvent
        extends
            ProjectEvent permits RunPipelineStageStartedEvent, RunPipelineStageFinishedEvent, RunPipelineStageFailedEvent {

    PipelineExecutionId executionId();
}
