package edu.stanford.protege.robot.service.message;

import edu.stanford.protege.robot.pipeline.PipelineExecutionId;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.common.Response;
import javax.annotation.Nonnull;

/**
 * Response message for ROBOT command execution.
 *
 * @param projectId
 *          WebProtege project unique identifier
 * @param pipelineExecutionId
 *          the unique identifier of a pipeline execution process
 */
public record ExecuteRobotCommandsResponse(
    @Nonnull ProjectId projectId,
    @Nonnull PipelineExecutionId pipelineExecutionId) implements Response {
}
