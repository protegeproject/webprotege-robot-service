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

@Component
public class PipelineSuccessResultRepository {

    private static final String COLLECTION_NAME = "RobotPipelineSuccessResult";
    private static final String FIELD_ID = "_id";
    private static final String FIELD_PIPELINE_EXECUTION_ID = "executionId";

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    public PipelineSuccessResultRepository(@Nonnull MongoTemplate mongoTemplate, @Nonnull ObjectMapper objectMapper) {
        this.mongoTemplate = Objects.requireNonNull(mongoTemplate, "MongoTemplate must not be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "ObjectMapper must not be null");
    }

    public void saveResult(@Nonnull PipelineSuccessResult result) {
        Objects.requireNonNull(result, "result cannot be null");

        var document = objectMapper.convertValue(result, Document.class);
        document.put(FIELD_ID, result.pipelineExecutionId().id());
        document.remove(FIELD_PIPELINE_EXECUTION_ID);
        mongoTemplate.save(document, COLLECTION_NAME);
    }

    public Optional<PipelineSuccessResult> findResult(@Nonnull PipelineExecutionId executionId) {
        Objects.requireNonNull(executionId, "executionId cannot be null");

        var query = Query.query(Criteria.where(FIELD_ID).is(executionId.id()));
        var document = mongoTemplate.findOne(query, Document.class, COLLECTION_NAME);
        if (document == null) {
            return Optional.empty();
        }
        return Optional.of(convertDocumentToPipelineSuccessResult(document));
    }

    /**
     * Converts a MongoDB Document to a PipelineSuccessResult, restoring the executionId from
     * _id.
     */
    private PipelineSuccessResult convertDocumentToPipelineSuccessResult(Document document) {
        document.put(FIELD_PIPELINE_EXECUTION_ID, document.get(FIELD_ID));
        return objectMapper.convertValue(document, PipelineSuccessResult.class);
    }
}
