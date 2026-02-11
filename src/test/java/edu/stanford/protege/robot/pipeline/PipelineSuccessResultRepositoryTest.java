package edu.stanford.protege.robot.pipeline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.robot.command.annotate.PlainAnnotation;
import edu.stanford.protege.robot.command.annotate.RobotAnnotateCommand;
import edu.stanford.protege.robot.config.TestMongoConfiguration;
import edu.stanford.protege.robot.service.config.JacksonConfiguration;
import edu.stanford.protege.webprotege.common.BlobLocation;
import edu.stanford.protege.webprotege.common.ProjectId;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration tests for {@link PipelineSuccessResultRepository} using Testcontainers.
 */
@DataMongoTest
@AutoConfigureJson
@Testcontainers
@Import({JacksonConfiguration.class, TestMongoConfiguration.class})
class PipelineSuccessResultRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private PipelineSuccessResultRepository repository;

    @BeforeEach
    void setUp() {
        repository = new PipelineSuccessResultRepository(mongoTemplate, objectMapper);
        // Clear the collection before each test
        mongoTemplate.dropCollection("RobotPipelineSuccessResult");
    }

    @Test
    void testSaveResult_Simple() {
        // Given
        var executionId = PipelineExecutionId.generate();
        var projectId = ProjectId.generate();
        var pipelineId = PipelineId.generate();
        var startTime = Instant.now().minusSeconds(60);
        var endTime = Instant.now();
        var pipeline = createSamplePipeline(projectId, pipelineId);
        var outputFiles = Map.<RelativePath, BlobLocation>of();

        var result = PipelineSuccessResult.create(
                executionId,
                projectId,
                1L,
                pipeline,
                startTime,
                endTime,
                outputFiles);

        // When
        repository.saveResult(result);

        // Then
        var found = repository.findResult(executionId);
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(result);
        assertThat(found.get().pipelineExecutionId()).isEqualTo(executionId);
        assertThat(found.get().projectId()).isEqualTo(projectId);
        assertThat(found.get().revisionNumber()).isEqualTo(1L);
        assertThat(found.get().startTimestamp()).isEqualTo(startTime);
        assertThat(found.get().endTimestamp()).isEqualTo(endTime);
    }

    @Test
    void testSaveResult_WithOutputFiles() {
        // Given
        var executionId = PipelineExecutionId.generate();
        var projectId = ProjectId.generate();
        var pipelineId = PipelineId.generate();
        var startTime = Instant.now().minusSeconds(60);
        var endTime = Instant.now();
        var pipeline = createSamplePipeline(projectId, pipelineId);

        var outputPath1 = new RelativePath("output/stage1.owl");
        var outputPath2 = new RelativePath("output/stage2.ttl");
        var blobLocation1 = new BlobLocation("bucket1", "object-key-1");
        var blobLocation2 = new BlobLocation("bucket2", "object-key-2");
        var outputFiles = Map.of(
                outputPath1, blobLocation1,
                outputPath2, blobLocation2);

        var result = PipelineSuccessResult.create(
                executionId,
                projectId,
                5L,
                pipeline,
                startTime,
                endTime,
                outputFiles);

        // When
        repository.saveResult(result);

        // Then
        var found = repository.findResult(executionId);
        assertThat(found).isPresent();
        assertThat(found.get().outputFiles()).hasSize(2);
        // Note: MongoDB replaces dots in map keys with _DOT_ during serialization
        var expectedPath1 = new RelativePath("output/stage1_DOT_owl");
        var expectedPath2 = new RelativePath("output/stage2_DOT_ttl");
        assertThat(found.get().outputFiles()).containsEntry(expectedPath1, blobLocation1);
        assertThat(found.get().outputFiles()).containsEntry(expectedPath2, blobLocation2);
    }

    @Test
    void testSaveResult_WithComplexPipeline() {
        // Given
        var executionId = PipelineExecutionId.generate();
        var projectId = ProjectId.generate();
        var pipelineId = PipelineId.generate();
        var startTime = Instant.now().minusSeconds(120);
        var endTime = Instant.now();

        // Create a more complex pipeline with multiple stages
        var pipeline = createComplexPipeline(projectId, pipelineId);

        var outputPath1 = new RelativePath("output/annotated.owl");
        var outputPath2 = new RelativePath("output/filtered.owl");
        var outputPath3 = new RelativePath("output/converted.ttl");
        var blobLocation1 = new BlobLocation("robot-outputs", "exec-1/annotated.owl");
        var blobLocation2 = new BlobLocation("robot-outputs", "exec-1/filtered.owl");
        var blobLocation3 = new BlobLocation("robot-outputs", "exec-1/converted.ttl");

        var outputFiles = Map.of(
                outputPath1, blobLocation1,
                outputPath2, blobLocation2,
                outputPath3, blobLocation3);

        var result = PipelineSuccessResult.create(
                executionId,
                projectId,
                10L,
                pipeline,
                startTime,
                endTime,
                outputFiles);

        // When
        repository.saveResult(result);

        // Then
        var found = repository.findResult(executionId);
        assertThat(found).isPresent();
        assertThat(found.get().executedPipeline().stages()).hasSize(3);
        assertThat(found.get().executedPipeline().label()).isEqualTo("Complex Pipeline");
        assertThat(found.get().outputFiles()).hasSize(3);
        assertThat(found.get().revisionNumber()).isEqualTo(10L);
        // Note: MongoDB replaces dots in map keys with _DOT_ during serialization
        // Verify the output files are stored (with transformed keys)
        assertThat(found.get().outputFiles().values())
                .containsExactlyInAnyOrder(blobLocation1, blobLocation2, blobLocation3);
    }

    @Test
    void testSaveResult_Update() {
        // Given - save initial result
        var executionId = PipelineExecutionId.generate();
        var projectId = ProjectId.generate();
        var pipelineId = PipelineId.generate();
        var startTime = Instant.now().minusSeconds(60);
        var endTime = Instant.now();
        var pipeline = createSamplePipeline(projectId, pipelineId);

        var initialResult = PipelineSuccessResult.create(
                executionId,
                projectId,
                1L,
                pipeline,
                startTime,
                endTime,
                Map.of());

        repository.saveResult(initialResult);

        // When - update with new output files
        var outputPath = new RelativePath("output/updated.owl");
        var blobLocation = new BlobLocation("bucket", "updated-key");
        var updatedResult = PipelineSuccessResult.create(
                executionId,
                projectId,
                1L,
                pipeline,
                startTime,
                endTime,
                Map.of(outputPath, blobLocation));

        repository.saveResult(updatedResult);

        // Then
        var found = repository.findResult(executionId);
        assertThat(found).isPresent();
        assertThat(found.get().outputFiles()).hasSize(1);
        // Note: MongoDB replaces dots in map keys with _DOT_ during serialization
        var expectedPath = new RelativePath("output/updated_DOT_owl");
        assertThat(found.get().outputFiles()).containsEntry(expectedPath, blobLocation);
    }

    @Test
    void testFindResult_NotFound() {
        // Given
        var nonExistentId = PipelineExecutionId.generate();

        // When
        var found = repository.findResult(nonExistentId);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void testSaveResult_NullResult() {
        // When/Then
        assertThatThrownBy(() -> repository.saveResult(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("result cannot be null");
    }

    @Test
    void testFindResult_NullExecutionId() {
        // When/Then
        assertThatThrownBy(() -> repository.findResult(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("executionId cannot be null");
    }

    @Test
    void testSaveResult_PreservesRevisionNumber() {
        // Given
        var executionId = PipelineExecutionId.generate();
        var projectId = ProjectId.generate();
        var pipelineId = PipelineId.generate();
        var startTime = Instant.now().minusSeconds(60);
        var endTime = Instant.now();
        var pipeline = createSamplePipeline(projectId, pipelineId);
        var revisionNumber = 42L;

        var result = PipelineSuccessResult.create(
                executionId,
                projectId,
                revisionNumber,
                pipeline,
                startTime,
                endTime,
                Map.of());

        // When
        repository.saveResult(result);

        // Then
        var found = repository.findResult(executionId);
        assertThat(found).isPresent();
        assertThat(found.get().revisionNumber()).isEqualTo(revisionNumber);
    }

    @Test
    void testSaveResult_PreservesTimestamps() {
        // Given
        var executionId = PipelineExecutionId.generate();
        var projectId = ProjectId.generate();
        var pipelineId = PipelineId.generate();
        var startTime = Instant.parse("2024-01-15T10:00:00Z");
        var endTime = Instant.parse("2024-01-15T10:05:30Z");
        var pipeline = createSamplePipeline(projectId, pipelineId);

        var result = PipelineSuccessResult.create(
                executionId,
                projectId,
                1L,
                pipeline,
                startTime,
                endTime,
                Map.of());

        // When
        repository.saveResult(result);

        // Then
        var found = repository.findResult(executionId);
        assertThat(found).isPresent();
        assertThat(found.get().startTimestamp()).isEqualTo(startTime);
        assertThat(found.get().endTimestamp()).isEqualTo(endTime);
    }

    @Test
    void testSaveResult_PreservesPipeline() {
        // Given
        var executionId = PipelineExecutionId.generate();
        var projectId = ProjectId.generate();
        var pipelineId = PipelineId.generate();
        var startTime = Instant.now().minusSeconds(60);
        var endTime = Instant.now();
        var pipeline = createSamplePipeline(projectId, pipelineId);

        var result = PipelineSuccessResult.create(
                executionId,
                projectId,
                1L,
                pipeline,
                startTime,
                endTime,
                Map.of());

        // When
        repository.saveResult(result);

        // Then
        var found = repository.findResult(executionId);
        assertThat(found).isPresent();
        assertThat(found.get().executedPipeline()).isNotNull();
        assertThat(found.get().executedPipeline().pipelineId()).isEqualTo(pipelineId);
        assertThat(found.get().executedPipeline().projectId()).isEqualTo(projectId);
        assertThat(found.get().executedPipeline().label()).isEqualTo("Test Pipeline");
        assertThat(found.get().executedPipeline().stages()).hasSize(2);
    }

    @Test
    void testSaveResult_EmptyOutputFiles() {
        // Given
        var executionId = PipelineExecutionId.generate();
        var projectId = ProjectId.generate();
        var pipelineId = PipelineId.generate();
        var startTime = Instant.now().minusSeconds(60);
        var endTime = Instant.now();
        var pipeline = createSamplePipeline(projectId, pipelineId);

        var result = PipelineSuccessResult.create(
                executionId,
                projectId,
                1L,
                pipeline,
                startTime,
                endTime,
                Map.of());

        // When
        repository.saveResult(result);

        // Then
        var found = repository.findResult(executionId);
        assertThat(found).isPresent();
        assertThat(found.get().outputFiles()).isEmpty();
    }

    @Test
    void testMultipleSaveAndFind() {
        // Given - multiple results
        var executionId1 = PipelineExecutionId.generate();
        var executionId2 = PipelineExecutionId.generate();
        var executionId3 = PipelineExecutionId.generate();
        var projectId = ProjectId.generate();
        var pipelineId = PipelineId.generate();
        var pipeline = createSamplePipeline(projectId, pipelineId);

        var result1 = PipelineSuccessResult.create(
                executionId1, projectId, 1L, pipeline, Instant.now(), Instant.now(), Map.of());
        var result2 = PipelineSuccessResult.create(
                executionId2, projectId, 2L, pipeline, Instant.now(), Instant.now(), Map.of());
        var result3 = PipelineSuccessResult.create(
                executionId3, projectId, 3L, pipeline, Instant.now(), Instant.now(), Map.of());

        // When
        repository.saveResult(result1);
        repository.saveResult(result2);
        repository.saveResult(result3);

        // Then
        assertThat(repository.findResult(executionId1)).isPresent();
        assertThat(repository.findResult(executionId2)).isPresent();
        assertThat(repository.findResult(executionId3)).isPresent();
        assertThat(repository.findResult(executionId1).get().revisionNumber()).isEqualTo(1L);
        assertThat(repository.findResult(executionId2).get().revisionNumber()).isEqualTo(2L);
        assertThat(repository.findResult(executionId3).get().revisionNumber()).isEqualTo(3L);
    }

    /**
     * Helper method to create a sample pipeline for testing.
     */
    private RobotPipeline createSamplePipeline(ProjectId projectId, PipelineId pipelineId) {
        var annotateCommand = new RobotAnnotateCommand(
                IRI.create("http://example.org/test"),
                null,
                List.of(new PlainAnnotation("rdfs:label", "Test")));

        var stage1 = new RobotPipelineStage(
                PipelineStageId.generate(),
                "Stage 1",
                "First stage",
                annotateCommand,
                new RelativePath("output/stage1.owl"));
        var stage2 = new RobotPipelineStage(
                PipelineStageId.generate(),
                "Stage 2",
                "Second stage",
                annotateCommand,
                null);

        return new RobotPipeline(
                projectId,
                pipelineId,
                "Test Pipeline",
                "Test Description",
                List.of(stage1, stage2));
    }

    /**
     * Helper method to create a more complex pipeline for testing.
     */
    private RobotPipeline createComplexPipeline(ProjectId projectId, PipelineId pipelineId) {
        var annotateCommand = new RobotAnnotateCommand(
                IRI.create("http://example.org/test"),
                IRI.create("http://example.org/test/v1"),
                List.of(
                        new PlainAnnotation("rdfs:label", "Test Ontology"),
                        new PlainAnnotation("dc:description", "Test Description")));

        var stage1 = new RobotPipelineStage(
                PipelineStageId.generate(),
                "Annotate",
                "Add metadata",
                annotateCommand,
                new RelativePath("output/annotated.owl"));
        var stage2 = new RobotPipelineStage(
                PipelineStageId.generate(),
                "Filter",
                "Filter content",
                annotateCommand,
                new RelativePath("output/filtered.owl"));
        var stage3 = new RobotPipelineStage(
                PipelineStageId.generate(),
                "Convert",
                "Convert format",
                annotateCommand,
                new RelativePath("output/converted.ttl"));

        return new RobotPipeline(
                projectId,
                pipelineId,
                "Complex Pipeline",
                "Multi-stage processing",
                List.of(stage1, stage2, stage3));
    }
}
