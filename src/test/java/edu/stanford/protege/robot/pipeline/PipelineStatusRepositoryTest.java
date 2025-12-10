package edu.stanford.protege.robot.pipeline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.robot.command.annotate.PlainAnnotation;
import edu.stanford.protege.robot.command.annotate.RobotAnnotateCommand;
import edu.stanford.protege.robot.service.config.JacksonConfiguration;
import edu.stanford.protege.webprotege.common.ProjectId;
import java.time.Instant;
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
 * Integration tests for {@link PipelineStatusRepository} using Testcontainers.
 */
@DataMongoTest
@AutoConfigureJson
@Testcontainers
@Import({JacksonConfiguration.class})
class PipelineStatusRepositoryTest {

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

  private PipelineStatusRepository repository;

  @BeforeEach
  void setUp() {
    repository = new PipelineStatusRepository(mongoTemplate, objectMapper);
    // Clear the collection before each test
    mongoTemplate.dropCollection("RobotPipelineStatus");
  }

  @Test
  void testSaveStatus_Initial() {
    // Given
    var executionId = PipelineExecutionId.generate();
    var pipelineId = PipelineId.generate();
    var startTime = Instant.now();
    var pipeline = createSamplePipeline(pipelineId);
    var status = PipelineStatus.create(executionId, pipelineId, startTime, pipeline);

    // When
    repository.saveStatus(status);

    // Then
    var found = repository.findStatus(executionId);
    assertThat(found).isPresent();
    assertThat(found.get()).usingRecursiveComparison().isEqualTo(status);
    assertThat(found.get().isRunning()).isTrue();
    assertThat(found.get().stages()).hasSize(2);
    assertThat(found.get().stages()).allMatch(stage -> stage.status() == StageStatus.WAITING);
  }

  @Test
  void testSaveStatus_withStageRunning() {
    // Given
    var executionId = PipelineExecutionId.generate();
    var pipelineId = PipelineId.generate();
    var startTime = Instant.now();
    var pipeline = createSamplePipeline(pipelineId);
    var initialStatus = PipelineStatus.create(executionId, pipelineId, startTime, pipeline);

    // When - mark first stage as running
    var stageId = pipeline.stages().get(0).stageId();
    var updatedStatus = PipelineStatus.withStageRunning(initialStatus, stageId);
    repository.saveStatus(updatedStatus);

    // Then
    var found = repository.findStatus(executionId);
    assertThat(found).isPresent();
    assertThat(found.get().stages().get(0).status()).isEqualTo(StageStatus.RUNNING);
    assertThat(found.get().stages().get(1).status()).isEqualTo(StageStatus.WAITING);
    assertThat(found.get().isRunning()).isTrue();
  }

  @Test
  void testSaveStatus_withStageSuccess() {
    // Given
    var executionId = PipelineExecutionId.generate();
    var pipelineId = PipelineId.generate();
    var startTime = Instant.now();
    var pipeline = createSamplePipeline(pipelineId);
    var initialStatus = PipelineStatus.create(executionId, pipelineId, startTime, pipeline);
    var stageId = pipeline.stages().get(0).stageId();
    var runningStatus = PipelineStatus.withStageRunning(initialStatus, stageId);

    // When - mark stage as successful
    var successStatus = PipelineStatus.withStageSuccess(runningStatus, stageId);
    repository.saveStatus(successStatus);

    // Then
    var found = repository.findStatus(executionId);
    assertThat(found).isPresent();
    assertThat(found.get().stages().get(0).status()).isEqualTo(StageStatus.FINISHED_WITH_SUCCESS);
    assertThat(found.get().stages().get(1).status()).isEqualTo(StageStatus.WAITING);
  }

  @Test
  void testSaveStatus_withStageError() {
    // Given
    var executionId = PipelineExecutionId.generate();
    var pipelineId = PipelineId.generate();
    var startTime = Instant.now();
    var pipeline = createSamplePipeline(pipelineId);
    var initialStatus = PipelineStatus.create(executionId, pipelineId, startTime, pipeline);
    var stageId = pipeline.stages().get(0).stageId();
    var runningStatus = PipelineStatus.withStageRunning(initialStatus, stageId);

    // When - mark stage as failed
    var errorStatus = PipelineStatus.withStageError(runningStatus, stageId);
    repository.saveStatus(errorStatus);

    // Then
    var found = repository.findStatus(executionId);
    assertThat(found).isPresent();
    assertThat(found.get().stages().get(0).status()).isEqualTo(StageStatus.FINISHED_WITH_ERROR);
    assertThat(found.get().isFailed()).isTrue();
  }

  @Test
  void testSaveStatus_withEndTime() {
    // Given
    var executionId = PipelineExecutionId.generate();
    var pipelineId = PipelineId.generate();
    var startTime = Instant.now().minusSeconds(60);
    var pipeline = createSamplePipeline(pipelineId);
    var status = PipelineStatus.create(executionId, pipelineId, startTime, pipeline);

    // When - set end time
    var endTime = Instant.now();
    var finishedStatus = PipelineStatus.withEndTime(status, endTime);
    repository.saveStatus(finishedStatus);

    // Then
    var found = repository.findStatus(executionId);
    assertThat(found).isPresent();
    assertThat(found.get().endTime()).isEqualTo(endTime);
  }

  @Test
  void testSaveStatus_CompleteWorkflow() {
    // Given
    var executionId = PipelineExecutionId.generate();
    var pipelineId = PipelineId.generate();
    var startTime = Instant.now().minusSeconds(60);
    var pipeline = createSamplePipeline(pipelineId);

    // Initial status with all stages waiting
    var status = PipelineStatus.create(executionId, pipelineId, startTime, pipeline);
    repository.saveStatus(status);

    // Stage 1 running
    var stage1Id = pipeline.stages().get(0).stageId();
    status = PipelineStatus.withStageRunning(status, stage1Id);
    repository.saveStatus(status);

    // Stage 1 success
    status = PipelineStatus.withStageSuccess(status, stage1Id);
    repository.saveStatus(status);

    // Stage 2 running
    var stage2Id = pipeline.stages().get(1).stageId();
    status = PipelineStatus.withStageRunning(status, stage2Id);
    repository.saveStatus(status);

    // Stage 2 success
    status = PipelineStatus.withStageSuccess(status, stage2Id);
    repository.saveStatus(status);

    // Set end time
    var endTime = Instant.now();
    status = PipelineStatus.withEndTime(status, endTime);
    repository.saveStatus(status);

    // Then
    var found = repository.findStatus(executionId);
    assertThat(found).isPresent();
    assertThat(found.get().isSuccessful()).isTrue();
    assertThat(found.get().isFailed()).isFalse();
    assertThat(found.get().isRunning()).isFalse();
    assertThat(found.get().endTime()).isNotNull();
    assertThat(found.get().stages()).allMatch(stage -> stage.status() == StageStatus.FINISHED_WITH_SUCCESS);
  }

  @Test
  void testFindStatus_NotFound() {
    // Given
    var nonExistentId = PipelineExecutionId.generate();

    // When
    var found = repository.findStatus(nonExistentId);

    // Then
    assertThat(found).isEmpty();
  }

  @Test
  void testDeleteStatus_Success() {
    // Given
    var executionId = PipelineExecutionId.generate();
    var pipelineId = PipelineId.generate();
    var startTime = Instant.now();
    var pipeline = createSamplePipeline(pipelineId);
    var status = PipelineStatus.create(executionId, pipelineId, startTime, pipeline);
    repository.saveStatus(status);

    // When
    var deleted = repository.deleteStatus(executionId);

    // Then
    assertThat(deleted).isTrue();
    assertThat(repository.findStatus(executionId)).isEmpty();
  }

  @Test
  void testDeleteStatus_NotFound() {
    // Given
    var nonExistentId = PipelineExecutionId.generate();

    // When
    var deleted = repository.deleteStatus(nonExistentId);

    // Then
    assertThat(deleted).isFalse();
  }

  @Test
  void testSaveStatus_NullStatus() {
    // When/Then
    assertThatThrownBy(() -> repository.saveStatus(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("status cannot be null");
  }

  @Test
  void testFindStatus_NullExecutionId() {
    // When/Then
    assertThatThrownBy(() -> repository.findStatus(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("executionId cannot be null");
  }

  @Test
  void testDeleteStatus_NullExecutionId() {
    // When/Then
    assertThatThrownBy(() -> repository.deleteStatus(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("executionId cannot be null");
  }

  @Test
  void testSaveStatus_PreservesOutputPaths() {
    // Given
    var executionId = PipelineExecutionId.generate();
    var projectId = ProjectId.generate();
    var pipelineId = PipelineId.generate();
    var startTime = Instant.now();

    // Create pipeline with stages that have output paths
    var outputPath1 = new RelativePath("output/stage1.owl");
    var outputPath2 = new RelativePath("output/stage2.owl");

    var annotateCommand = new RobotAnnotateCommand(
        IRI.create("http://example.org/test"),
        null,
        List.of(new PlainAnnotation("rdfs:label", "Test")));

    var stage1 = new RobotPipelineStage(PipelineStageId.generate(), "Stage 1", "Desc", annotateCommand, outputPath1);
    var stage2 = new RobotPipelineStage(PipelineStageId.generate(), "Stage 2", "Desc", annotateCommand, outputPath2);

    var pipeline = new RobotPipeline(projectId, pipelineId, "Test Pipeline", "Desc", List.of(stage1, stage2));
    var status = PipelineStatus.create(executionId, pipelineId, startTime, pipeline);

    // When
    repository.saveStatus(status);
    var found = repository.findStatus(executionId);

    // Then
    assertThat(found).isPresent();
    assertThat(found.get().stages().get(0).outputFile()).isEqualTo(outputPath1);
    assertThat(found.get().stages().get(1).outputFile()).isEqualTo(outputPath2);
  }

  /**
   * Helper method to create a sample pipeline for testing.
   */
  private RobotPipeline createSamplePipeline(PipelineId pipelineId) {
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
        ProjectId.generate(),
        pipelineId,
        "Test Pipeline",
        "Test Description",
        List.of(stage1, stage2));
  }
}
