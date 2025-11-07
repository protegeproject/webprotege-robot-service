package edu.stanford.protege.robot.pipeline.event;

import static edu.stanford.protege.robot.pipeline.event.LoadOntologyStartedEvent.CHANNEL;

import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.robot.pipeline.PipelineExecutionId;
import edu.stanford.protege.robot.pipeline.PipelineId;
import edu.stanford.protege.webprotege.common.EventId;
import edu.stanford.protege.webprotege.common.ProjectId;
import javax.annotation.Nonnull;

@JsonTypeName(CHANNEL)
public record LoadOntologyStartedEvent(
    @Nonnull ProjectId projectId,
    @Nonnull PipelineExecutionId executionId,
    @Nonnull PipelineId pipelineId,
    @Nonnull EventId eventId) implements LoadOntologyEvent {

  public static final String CHANNEL = "webprotege.events.robot.LoadOntologyStarted";

  @Override
  public String getChannel() {
    return CHANNEL;
  }
}
