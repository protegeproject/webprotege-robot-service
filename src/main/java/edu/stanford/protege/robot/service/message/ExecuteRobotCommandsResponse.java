package edu.stanford.protege.robot.service.message;

import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.common.Response;
import java.nio.file.Path;
import javax.annotation.Nonnull;

/**
 * Response message for ROBOT command execution.
 *
 * <p>
 * This record represents the JSON response format from the ROBOT service, indicating success or
 * failure and providing the output path or error message.
 *
 * @param projectId
 *          WebProtege project unique identifier
 * @param outputOntologyPath
 *          absolute path to save the processed ontology
 */
public record ExecuteRobotCommandsResponse(
    @Nonnull ProjectId projectId,
    @Nonnull Path outputOntologyPath) implements Response {
}
