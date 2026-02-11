package edu.stanford.protege.robot.pipeline;

import edu.stanford.protege.webprotege.common.BlobLocation;
import edu.stanford.protege.webprotege.common.ProjectId;
import java.time.Instant;
import java.util.Map;

public record PipelineSuccessResult(PipelineExecutionId pipelineExecutionId,
        ProjectId projectId,
        long revisionNumber,
        RobotPipeline executedPipeline,
        Instant startTimestamp,
        Instant endTimestamp,
        Map<RelativePath, BlobLocation> outputFiles) {

    public static PipelineSuccessResult create(PipelineExecutionId executionId, ProjectId projectId,
            long revisionNumber,
            RobotPipeline executedPipeline,
            Instant startTimestamp, Instant endTimestamp, Map<RelativePath, BlobLocation> outputFiles) {
        return new PipelineSuccessResult(executionId, projectId, revisionNumber, executedPipeline, startTimestamp,
                endTimestamp,
                outputFiles);
    }
}
