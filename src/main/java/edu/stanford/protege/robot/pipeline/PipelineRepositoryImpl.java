package edu.stanford.protege.robot.pipeline;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import edu.stanford.protege.webprotege.common.ProjectId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class PipelineRepositoryImpl implements PipelineRepository {

    private static final String COLLECTION_NAME = "RobotPipelines";
    private static final String FIELD_ID = "_id";
    private static final String FIELD_PROJECT_ID = "projectId";
    private static final String FIELD_PIPELINE_ID = "pipelineId";

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    public PipelineRepositoryImpl(@Nonnull MongoTemplate mongoTemplate,
            @Nonnull ObjectMapper objectMapper) {
        this.mongoTemplate = Objects.requireNonNull(mongoTemplate, "mongoTemplate cannot be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper cannot be null");
    }

    /**
     * Finds all pipelines associated with a specific project.
     *
     * @param projectId
     *            the project ID to search for (must not be null)
     * @return list of all pipelines for the project (empty list if none found)
     * @throws NullPointerException
     *             if projectId is null
     */
    @Override
    public List<RobotPipeline> findPipelines(@Nonnull ProjectId projectId) {
        Objects.requireNonNull(projectId, "projectId cannot be null");

        var query = Query.query(Criteria.where(FIELD_PROJECT_ID).is(projectId.id()));
        var documents = mongoTemplate.find(query, Document.class, COLLECTION_NAME);
        return documents.stream()
                .map(this::convertDocumentToPipeline)
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * Deletes all pipelines associated with a specific project.
     *
     * <p>
     * Removes all pipeline documents where the {@code projectId.id} field matches the provided
     * project ID.
     *
     * @param projectId
     *            the project ID whose pipelines should be deleted (must not be null)
     * @throws NullPointerException
     *             if projectId is null
     */
    @Override
    public void deletePipelines(@Nonnull ProjectId projectId) {
        Objects.requireNonNull(projectId, "projectId cannot be null");

        var query = Query.query(Criteria.where(FIELD_PROJECT_ID).is(projectId.id()));
        mongoTemplate.remove(query, COLLECTION_NAME);
    }

    /**
     * Finds a single pipeline by its unique pipeline ID.
     *
     * <p>
     * Queries the database for a pipeline document with the {@code _id} field matching the provided
     * pipeline ID.
     *
     * @param pipelineId
     *            the pipeline ID to search for (must not be null)
     * @return Optional containing the pipeline if found, empty Optional otherwise
     * @throws NullPointerException
     *             if pipelineId is null
     */
    @Override
    public Optional<RobotPipeline> findPipeline(@Nonnull PipelineId pipelineId) {
        Objects.requireNonNull(pipelineId, "pipelineId cannot be null");

        var query = Query.query(Criteria.where(FIELD_ID).is(pipelineId.id()));
        var document = mongoTemplate.findOne(query, Document.class, COLLECTION_NAME);
        if (document == null) {
            return Optional.empty();
        }
        return Optional.of(convertDocumentToPipeline(document));
    }

    /**
     * Deletes a single pipeline by its unique pipeline ID.
     *
     * <p>
     * Removes the pipeline document with the {@code _id} field matching the provided pipeline ID.
     *
     * @param pipelineId
     *            the pipeline ID to delete (must not be null)
     * @throws NullPointerException
     *             if pipelineId is null
     */
    @Override
    public void deletePipeline(@Nonnull PipelineId pipelineId) {
        Objects.requireNonNull(pipelineId, "pipelineId cannot be null");

        var query = Query.query(Criteria.where(FIELD_ID).is(pipelineId.id()));
        mongoTemplate.remove(query, COLLECTION_NAME);
    }

    /**
     * Saves or updates multiple pipelines in the database.
     *
     * <p>
     * This method performs an upsert operation for each pipeline. If a pipeline with the same
     * PipelineId already exists, it will be replaced. Otherwise, a new document is inserted.
     *
     * @param pipelines
     *            the list of pipelines to save (must not be null)
     * @throws NullPointerException
     *             if input pipelines is null
     */
    @Override
    public void savePipelines(@Nonnull List<RobotPipeline> pipelines) {
        Objects.requireNonNull(pipelines, "pipelines cannot be null");

        for (var pipeline : pipelines) {
            var document = objectMapper.convertValue(pipeline, Document.class);
            document.put(FIELD_ID, pipeline.pipelineId().id());
            document.remove(FIELD_PIPELINE_ID);
            mongoTemplate.save(document, COLLECTION_NAME);
        }
    }

    /**
     * Converts a MongoDB Document to a RobotPipeline, restoring the pipelineId from _id.
     */
    private RobotPipeline convertDocumentToPipeline(Document document) {
        document.put(FIELD_PIPELINE_ID, document.get(FIELD_ID));
        return objectMapper.convertValue(document, RobotPipeline.class);
    }
}
