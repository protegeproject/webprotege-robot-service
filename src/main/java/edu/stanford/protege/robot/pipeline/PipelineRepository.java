package edu.stanford.protege.robot.pipeline;

import edu.stanford.protege.webprotege.common.ProjectId;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for persisting and retrieving ROBOT pipelines.
 *
 * <p>
 * This repository provides CRUD operations for managing {@link RobotPipeline} entities, which
 * represent sequences of ROBOT commands that can be executed on ontologies. Pipelines are
 * organized by project and can be queried either by project ID (to retrieve all pipelines for a
 * project) or by pipeline ID (to retrieve a specific pipeline).
 */
public interface PipelineRepository {

    /**
     * Finds all pipelines associated with a specific project.
     *
     * <p>
     * This method retrieves all pipeline configurations that have been created for the given
     * project. The returned list may be empty if no pipelines exist for the project.
     *
     * @param projectId
     *            the unique identifier of the project whose pipelines should be retrieved (must not be
     *            null)
     * @return an immutable list of all pipelines for the project, or an empty list if none exist
     */
    List<RobotPipeline> findPipelines(ProjectId projectId);

    /**
     * Deletes all pipelines associated with a specific project.
     *
     * <p>
     * This method removes all pipeline configurations for the given project. If no pipelines exist
     * for the project, this method completes successfully without error.
     *
     * @param projectId
     *            the unique identifier of the project whose pipelines should be deleted (must not be
     *            null)
     */
    void deletePipelines(ProjectId projectId);

    /**
     * Finds a specific pipeline by its unique pipeline ID.
     *
     * <p>
     * This method retrieves a single pipeline configuration using its globally unique pipeline ID,
     * regardless of which project it belongs to.
     *
     * @param pipelineId
     *            the unique identifier of the pipeline to retrieve (must not be null)
     * @return an {@link Optional} containing the pipeline if found, or {@link Optional#empty()} if
     *         no pipeline exists with the given ID
     */
    Optional<RobotPipeline> findPipeline(PipelineId pipelineId);

    /**
     * Deletes a specific pipeline by its unique pipeline ID.
     *
     * <p>
     * This method removes a single pipeline configuration. If no pipeline exists with the given ID,
     * this method completes successfully without error.
     *
     * @param pipelineId
     *            the unique identifier of the pipeline to delete (must not be null)
     */
    void deletePipeline(PipelineId pipelineId);

    /**
     * Saves or updates one or more pipelines.
     *
     * <p>
     * This method performs an upsert operation for each pipeline in the list. If a pipeline with the
     * same {@link PipelineId} already exists, it will be replaced with the new version. Otherwise, a
     * new pipeline will be inserted.
     *
     * @param pipelines
     *            the list of pipelines to save or update (must not be null, may be empty)
     */
    void savePipelines(List<RobotPipeline> pipelines);
}
