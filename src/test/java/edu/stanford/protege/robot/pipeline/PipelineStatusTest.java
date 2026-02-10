package edu.stanford.protege.robot.pipeline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import edu.stanford.protege.robot.command.RobotCommand;
import edu.stanford.protege.webprotege.common.ProjectId;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class PipelineStatusTest {

  private RobotPipeline pipelineWithSingleStage() {
    RobotCommand command = mock(RobotCommand.class);
    var stage = new RobotPipelineStage(PipelineStageId.generate(), null, null, command, null);
    return new RobotPipeline(ProjectId.generate(), PipelineId.generate(), null, null, List.of(stage));
  }

  @Test
  void isRunning_trueWhenPreparationWaiting() {
    var pipeline = pipelineWithSingleStage();
    var status = PipelineStatus.createWithPreparationStatus(PipelineExecutionId.generate(), pipeline.pipelineId(),
        Instant.now(), pipeline, PipelinePreparationStatus.waiting("prepping"));

    assertThat(status.isRunning()).isTrue();
  }

  @Test
  void isSuccessful_falseWhenPreparationFailedEvenIfStageSucceeded() {
    var pipeline = pipelineWithSingleStage();
    var status = PipelineStatus.createWithPreparationStatus(PipelineExecutionId.generate(), pipeline.pipelineId(),
        Instant.now(), pipeline, PipelinePreparationStatus.finishedWithError("boom"));
    var stageId = pipeline.stages().get(0).stageId();
    status = PipelineStatus.withStageSuccess(status, stageId);

    assertThat(status.isSuccessful()).isFalse();
  }

  @Test
  void isFailed_trueWhenPreparationFailed() {
    var pipeline = pipelineWithSingleStage();
    var status = PipelineStatus.createWithPreparationStatus(PipelineExecutionId.generate(), pipeline.pipelineId(),
        Instant.now(), pipeline, PipelinePreparationStatus.finishedWithError("boom"));

    assertThat(status.isFailed()).isTrue();
  }
}
