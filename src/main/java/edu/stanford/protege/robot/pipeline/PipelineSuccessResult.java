package edu.stanford.protege.robot.pipeline;

import edu.stanford.protege.webprotege.common.BlobLocation;
import edu.stanford.protege.webprotege.common.ProjectId;
import java.time.Instant;
import java.util.Map;

public record PipelineSuccessResult(ProjectId projectId,
    long revisionNumber,
    RobotPipeline executedPipeline,
    Instant startTimestamp,
    Instant endTimestamp,
    Map<RelativePath, BlobLocation> outputFiles) {

  public static PipelineSuccessResult create(ProjectId projectId, long revisionNumber, RobotPipeline executedPipeline,
      Instant startTimestamp, Instant endTimestamp, Map<RelativePath, BlobLocation> outputFiles) {
    return new PipelineSuccessResult(projectId, revisionNumber, executedPipeline, startTimestamp, endTimestamp,
        outputFiles);
  }
}
