package edu.stanford.protege.robot.pipeline;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * Repository for persisting and retrieving pipeline execution status in MongoDB.
 *
 * <p>
 * This repository stores {@link PipelineStatus} objects to track the execution state
 * of ROBOT pipelines, including start/end timestamps and success/failure status.
 */
@Component
public class PipelineStatusRepository {

    private static final String COLLECTION_NAME = "RobotPipelineStatus";
    private static final String FIELD_ID = "_id";
    private static final String FIELD_PIPELINE_EXECUTION_ID = "executionId";

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    public PipelineStatusRepository(@Nonnull MongoTemplate mongoTemplate, @Nonnull ObjectMapper objectMapper) {
        this.mongoTemplate = Objects.requireNonNull(mongoTemplate, "MongoTemplate must not be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "ObjectMapper must not be null");
    }

    public void saveStatus(@Nonnull PipelineStatus status) {
        Objects.requireNonNull(status, "status cannot be null");

        var document = objectMapper.convertValue(status, Document.class);
        document.put(FIELD_ID, status.executionId().id());
        document.remove(FIELD_PIPELINE_EXECUTION_ID);
        mongoTemplate.save(document, COLLECTION_NAME);
    }

    public Optional<PipelineStatus> findStatus(@Nonnull PipelineExecutionId executionId) {
        Objects.requireNonNull(executionId, "executionId cannot be null");

        var query = Query.query(Criteria.where(FIELD_ID).is(executionId.id()));
        var document = mongoTemplate.findOne(query, Document.class, COLLECTION_NAME);
        if (document == null) {
            return Optional.empty();
        }
        return Optional.of(convertDocumentToPipelineStatus(document));
    }

    public boolean deleteStatus(@Nonnull PipelineExecutionId executionId) {
        Objects.requireNonNull(executionId, "executionId cannot be null");

        var query = Query.query(Criteria.where(FIELD_ID).is(executionId.id()));
        var result = mongoTemplate.remove(query, COLLECTION_NAME);
        return result.getDeletedCount() > 0;
    }

    /**
     * Converts a MongoDB Document to a PipelineStatus, restoring the executionId from _id.
     */
    private PipelineStatus convertDocumentToPipelineStatus(Document document) {
        document.put(FIELD_PIPELINE_EXECUTION_ID, document.get(FIELD_ID));
        return objectMapper.convertValue(document, PipelineStatus.class);
    }
}
