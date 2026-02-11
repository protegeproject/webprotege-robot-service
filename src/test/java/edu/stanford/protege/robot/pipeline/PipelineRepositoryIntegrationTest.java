package edu.stanford.protege.robot.pipeline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.robot.command.annotate.PlainAnnotation;
import edu.stanford.protege.robot.command.annotate.RobotAnnotateCommand;
import edu.stanford.protege.robot.command.extract.HandlingImports;
import edu.stanford.protege.robot.command.extract.MireotExtractStrategy;
import edu.stanford.protege.robot.command.extract.RobotExtractCommand;
import edu.stanford.protege.robot.service.config.JacksonConfiguration;
import edu.stanford.protege.webprotege.common.ProjectId;
import java.util.List;
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
 * Integration tests for {@link PipelineRepositoryImpl} using Testcontainers.
 */
@DataMongoTest
@AutoConfigureJson
@Testcontainers
@Import({JacksonConfiguration.class})
class PipelineRepositoryIntegrationTest {

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

    private PipelineRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new PipelineRepositoryImpl(mongoTemplate, objectMapper);
        // Clear the collection before each test
        mongoTemplate.dropCollection("RobotPipelines");
    }

    /**
     * Test saving a single pipeline and verifying it can be retrieved.
     */
    @Test
    void testSavePipeline_SinglePipeline() {
        // Given
        var projectId = ProjectId.generate();
        var pipelineId = PipelineId.generate();
        var pipeline = createSamplePipeline(projectId, pipelineId, "Test Pipeline", "A test pipeline");

        // When
        repository.savePipelines(List.of(pipeline));

        // Then
        var found = repository.findPipeline(pipelineId);
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(pipeline);
        assertThat(found.get().projectId()).isEqualTo(projectId);
        assertThat(found.get().label()).isEqualTo("Test Pipeline");
    }

    /**
     * Test saving multiple pipelines for the same project.
     */
    @Test
    void testSavePipelines_MultiplePipelines() {
        // Given
        var projectId = ProjectId.generate();
        var pipeline1 = createSamplePipeline(projectId, PipelineId.generate(), "Pipeline 1",
                "First pipeline");
        var pipeline2 = createSamplePipeline(projectId, PipelineId.generate(), "Pipeline 2",
                "Second pipeline");
        var pipeline3 = createSamplePipeline(projectId, PipelineId.generate(), "Pipeline 3",
                "Third pipeline");

        // When
        repository.savePipelines(List.of(pipeline1, pipeline2, pipeline3));

        // Then
        var foundPipelines = repository.findPipelines(projectId);
        assertThat(foundPipelines).hasSize(3);
        assertThat(foundPipelines).extracting(RobotPipeline::label)
                .containsExactlyInAnyOrder("Pipeline 1", "Pipeline 2", "Pipeline 3");
    }

    /**
     * Test upsert behavior - updating an existing pipeline.
     */
    @Test
    void testSavePipeline_Upsert() {
        // Given
        var projectId = ProjectId.generate();
        var pipelineId = PipelineId.generate();
        var originalPipeline = createSamplePipeline(projectId, pipelineId, "Original", "Original desc");

        // Save original
        repository.savePipelines(List.of(originalPipeline));

        // When - Save updated version with same ID
        var updatedPipeline = createSamplePipeline(projectId, pipelineId, "Updated", "Updated desc");
        repository.savePipelines(List.of(updatedPipeline));

        // Then - Should replace the original
        var found = repository.findPipeline(pipelineId);
        assertThat(found).isPresent();
        assertThat(found.get().label()).isEqualTo("Updated");
        assertThat(found.get().description()).isEqualTo("Updated desc");

        // Verify only one document exists
        var allPipelines = repository.findPipelines(projectId);
        assertThat(allPipelines).hasSize(1);
    }

    /**
     * Test finding pipelines by project ID.
     */
    @Test
    void testFindPipelines_ByProjectId() {
        // Given
        var project1 = ProjectId.generate();
        var project2 = ProjectId.generate();

        var p1_pipeline1 = createSamplePipeline(project1, PipelineId.generate(), "P1-1", "Desc");
        var p1_pipeline2 = createSamplePipeline(project1, PipelineId.generate(), "P1-2", "Desc");
        var p2_pipeline1 = createSamplePipeline(project2, PipelineId.generate(), "P2-1", "Desc");

        repository.savePipelines(List.of(p1_pipeline1, p1_pipeline2, p2_pipeline1));

        // When
        var project1Pipelines = repository.findPipelines(project1);
        var project2Pipelines = repository.findPipelines(project2);

        // Then
        assertThat(project1Pipelines).hasSize(2);
        assertThat(project1Pipelines).extracting(RobotPipeline::label)
                .containsExactlyInAnyOrder("P1-1", "P1-2");

        assertThat(project2Pipelines).hasSize(1);
        assertThat(project2Pipelines).extracting(RobotPipeline::label).containsExactly("P2-1");
    }

    /**
     * Test finding pipelines for a project with no pipelines.
     */
    @Test
    void testFindPipelines_EmptyResult() {
        // Given
        var projectId = ProjectId.generate();

        // When
        var pipelines = repository.findPipelines(projectId);

        // Then
        assertThat(pipelines).isEmpty();
    }

    /**
     * Test finding a single pipeline by pipeline ID.
     */
    @Test
    void testFindPipeline_ByPipelineId() {
        // Given
        var projectId = ProjectId.generate();
        var pipelineId = PipelineId.generate();
        var pipeline = createSamplePipeline(projectId, pipelineId, "Test", "Test desc");

        repository.savePipelines(List.of(pipeline));

        // When
        var found = repository.findPipeline(pipelineId);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(pipeline);
    }

    /**
     * Test finding a non-existent pipeline returns empty Optional.
     */
    @Test
    void testFindPipeline_NotFound() {
        // Given
        var nonexistentId = PipelineId.generate();

        // When
        var found = repository.findPipeline(nonexistentId);

        // Then
        assertThat(found).isEmpty();
    }

    /**
     * Test deleting all pipelines for a project.
     */
    @Test
    void testDeletePipelines_ByProjectId() {
        // Given
        var project1 = ProjectId.generate();
        var project2 = ProjectId.generate();

        var p1_pipeline1 = createSamplePipeline(project1, PipelineId.generate(), "P1-1", "Desc");
        var p1_pipeline2 = createSamplePipeline(project1, PipelineId.generate(), "P1-2", "Desc");
        var p2_pipeline1 = createSamplePipeline(project2, PipelineId.generate(), "P2-1", "Desc");

        repository.savePipelines(List.of(p1_pipeline1, p1_pipeline2, p2_pipeline1));

        // When
        repository.deletePipelines(project1);

        // Then
        var project1Pipelines = repository.findPipelines(project1);
        var project2Pipelines = repository.findPipelines(project2);

        assertThat(project1Pipelines).isEmpty();
        assertThat(project2Pipelines).hasSize(1);
    }

    /**
     * Test deleting a single pipeline by ID.
     */
    @Test
    void testDeletePipeline_ByPipelineId() {
        // Given
        var projectId = ProjectId.generate();
        var pipelineId1 = PipelineId.generate();
        var pipelineId2 = PipelineId.generate();

        var pipeline1 = createSamplePipeline(projectId, pipelineId1, "Pipeline 1", "Desc");
        var pipeline2 = createSamplePipeline(projectId, pipelineId2, "Pipeline 2", "Desc");

        repository.savePipelines(List.of(pipeline1, pipeline2));

        // When
        repository.deletePipeline(pipelineId1);

        // Then
        var found1 = repository.findPipeline(pipelineId1);
        var found2 = repository.findPipeline(pipelineId2);
        var allPipelines = repository.findPipelines(projectId);

        assertThat(found1).isEmpty();
        assertThat(found2).isPresent();
        assertThat(allPipelines).hasSize(1);
    }

    /**
     * Test that null projectId throws NullPointerException in findPipelines.
     */
    @Test
    void testFindPipelines_NullProjectId() {
        assertThatThrownBy(() -> repository.findPipelines(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("projectId cannot be null");
    }

    /**
     * Test that null pipelineId throws NullPointerException in findPipeline.
     */
    @Test
    void testFindPipeline_NullPipelineId() {
        assertThatThrownBy(() -> repository.findPipeline(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("pipelineId cannot be null");
    }

    /**
     * Test that null pipelines list throws NullPointerException in savePipelines.
     */
    @Test
    void testSavePipelines_NullList() {
        assertThatThrownBy(() -> repository.savePipelines(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("pipelines cannot be null");
    }

    /**
     * Test that null projectId throws NullPointerException in deletePipelines.
     */
    @Test
    void testDeletePipelines_NullProjectId() {
        assertThatThrownBy(() -> repository.deletePipelines(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("projectId cannot be null");
    }

    /**
     * Test that null pipelineId throws NullPointerException in deletePipeline.
     */
    @Test
    void testDeletePipeline_NullPipelineId() {
        assertThatThrownBy(() -> repository.deletePipeline(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("pipelineId cannot be null");
    }

    /**
     * Test saving empty list of pipelines.
     */
    @Test
    void testSavePipelines_EmptyList() {
        // Given
        var projectId = ProjectId.generate();

        // When
        repository.savePipelines(List.of());

        // Then
        var pipelines = repository.findPipelines(projectId);
        assertThat(pipelines).isEmpty();
    }

    /**
     * Test that complex pipeline stages with polymorphic commands serialize/deserialize correctly.
     */
    @Test
    void testSavePipeline_ComplexStages() {
        // Given
        var projectId = ProjectId.generate();
        var pipelineId = PipelineId.generate();

        // Create pipeline with multiple different command types
        var extractStrategy = new MireotExtractStrategy(
                List.of("http://example.org/term1"),
                List.of("http://example.org/upper1"),
                List.of("http://example.org/lower1"));
        var extractCommand = new RobotExtractCommand(extractStrategy, null, HandlingImports.exclude);

        var annotateCommand = new RobotAnnotateCommand(
                IRI.create("http://example.org/ontology"),
                IRI.create("http://example.org/ontology/v1.0"),
                List.of(new PlainAnnotation("rdfs:label", "Test Ontology")));

        var stage1 = new RobotPipelineStage(PipelineStageId.generate(), "Extract", "Extract subset", extractCommand,
                null);
        var stage2 = new RobotPipelineStage(PipelineStageId.generate(), "Annotate", "Add metadata", annotateCommand,
                null);

        var pipeline = new RobotPipeline(projectId, pipelineId, "Complex Pipeline",
                "Pipeline with multiple command types", List.of(stage1, stage2));

        // When
        repository.savePipelines(List.of(pipeline));

        // Then
        var found = repository.findPipeline(pipelineId);
        assertThat(found).isPresent();
        assertThat(found.get().stages()).hasSize(2);
        assertThat(found.get().stages().get(0).label()).isEqualTo("Extract");
        assertThat(found.get().stages().get(1).label()).isEqualTo("Annotate");
    }

    /**
     * Helper method to create a sample pipeline for testing.
     */
    private RobotPipeline createSamplePipeline(ProjectId projectId, PipelineId pipelineId,
            String label, String description) {
        var annotateCommand = new RobotAnnotateCommand(
                IRI.create("http://example.org/test"),
                null,
                List.of(new PlainAnnotation("rdfs:label", "Test")));

        var stage = new RobotPipelineStage(PipelineStageId.generate(), "Test Stage", "A test stage", annotateCommand,
                null);

        return new RobotPipeline(projectId, pipelineId, label, description, List.of(stage));
    }
}
