package edu.stanford.protege.robot.pipeline.event;

import static edu.stanford.protege.robot.pipeline.event.SaveOntologySucceededEvent.CHANNEL;

import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.robot.pipeline.PipelineExecutionId;
import edu.stanford.protege.robot.pipeline.PipelineId;
import edu.stanford.protege.webprotege.common.EventId;
import edu.stanford.protege.webprotege.common.ProjectId;
import javax.annotation.Nonnull;

@JsonTypeName(CHANNEL)
public record SaveOntologySucceededEvent(
        @Nonnull ProjectId projectId,
        @Nonnull PipelineExecutionId executionId,
        @Nonnull PipelineId pipelineId,
        @Nonnull EventId eventId) implements SaveOntologyEvent {

    public static final String CHANNEL = "webprotege.events.robot.SaveOntologySucceeded";

    @Override
    public String getChannel() {
        return CHANNEL;
    }
}
