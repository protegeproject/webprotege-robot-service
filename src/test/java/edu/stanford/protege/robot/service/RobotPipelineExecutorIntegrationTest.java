package edu.stanford.protege.robot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.robot.pipeline.PipelineExecutionId;
import edu.stanford.protege.robot.pipeline.PipelineLogger;
import edu.stanford.protege.robot.pipeline.RobotPipeline;
import edu.stanford.protege.robot.pipeline.RobotPipelineStage;
import edu.stanford.protege.robot.service.config.JacksonConfiguration;
import edu.stanford.protege.webprotege.common.ProjectId;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import javax.inject.Provider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.obolibrary.robot.CommandState;
import org.obolibrary.robot.IOHelper;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;

/**
 * Integration tests for {@link RobotPipelineExecutor}.
 *
 * <p>
 * These tests verify the full execution flow of ROBOT command pipelines with mocked I/O and
 * logging dependencies. Two comprehensive test cases are provided:
 *
 * <ul>
 * <li><b>Case 1:</b> Remove → Extract → Repair → Annotate (4 stages)</li>
 * <li><b>Case 2:</b> Relax → Remove → Filter → Repair → Annotate (5 stages)</li>
 * </ul>
 */
@JsonTest
@Import({JacksonConfiguration.class})
class RobotPipelineExecutorIntegrationTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Mock
  private IOHelper ioHelper;

  @Mock
  private PipelineLogger pipelineLogger;

  @Mock
  private Provider<CommandState> commandStateProvider;

  private RobotPipelineExecutor executor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    executor = new RobotPipelineExecutor(commandStateProvider, ioHelper, pipelineLogger);
  }

  /**
   * Test Case 1: Remove → Extract → Repair → Annotate
   *
   * <p>
   * This test verifies a 4-stage pipeline that:
   *
   * <ol>
   * <li>Removes annotation axioms from the ontology</li>
   * <li>Extracts a subset focusing on dairy products</li>
   * <li>Repairs invalid references and merges axiom annotations</li>
   * <li>Adds new metadata annotations</li>
   * </ol>
   */
  @Test
  void testExecutePipelineCase1_RemoveExtractRepairAnnotate() throws Exception {
    // Load and parse pipeline JSON
    var pipelineJson = getJsonContent("/integration/test-pipeline-case1.json");
    var pipeline = objectMapper.readValue(pipelineJson, RobotPipeline.class);

    // Verify pipeline structure
    assertThat(pipeline).isNotNull();
    assertThat(pipeline.stages()).hasSize(4);
    assertThat(pipeline.label()).isEqualTo("Test pipeline 1");

    // Verify stage labels
    assertThat(pipeline.stages().get(0).label()).isEqualTo("Remove Annotations");
    assertThat(pipeline.stages().get(1).label()).isEqualTo("Extract Dairy Products");
    assertThat(pipeline.stages().get(2).label()).isEqualTo("Repair Ontology");
    assertThat(pipeline.stages().get(3).label()).isEqualTo("Add Metadata");

    // Setup test paths - save to target/test-output for inspection
    var inputPath = getResourcePath("/integration/grocery-ontology.owl");
    var outputDir = Paths.get("target/test-output");
    Files.createDirectories(outputDir);
    var outputPath = outputDir.resolve("output-case1.owl");

    var commandState = new CommandState();

    // Create real IOHelper for actual loading/saving
    var realIOHelper = new IOHelper();

    // Mock IOHelper to delegate to real IOHelper
    when(ioHelper.loadOntology(any(File.class))).thenAnswer(invocation -> {
      File file = invocation.getArgument(0);
      return realIOHelper.loadOntology(file);
    });
    when(ioHelper.saveOntology(any(OWLOntology.class), anyString())).thenAnswer(invocation -> {
      OWLOntology ontology = invocation.getArgument(0);
      String savePath = invocation.getArgument(1);
      return realIOHelper.saveOntology(ontology, savePath);
    });

    // Mock Provider to return fresh CommandState
    when(commandStateProvider.get()).thenReturn(commandState);

    // Execute pipeline (use projectId from JSON)
    var result = executor.executePipeline(
        pipeline.projectId(),
        inputPath,
        outputPath,
        pipeline);

    // Verify results
    assertThat(result).isNotNull();
    var actualOntology = result.getOntology();
    assertThat(actualOntology).isNotNull();

    // Verify output was saved to file
    assertThat(outputPath).exists();

    // Verify expected classes were extracted
    var ontologyIRI = IRI.create("http://protege.stanford.edu/ontologies/groceries/");
    assertThat(actualOntology.containsClassInSignature(IRI.create(ontologyIRI + "DairyProduct"))).isTrue();
    assertThat(actualOntology.containsClassInSignature(IRI.create(ontologyIRI + "Butter"))).isTrue();
    assertThat(actualOntology.containsClassInSignature(IRI.create(ontologyIRI + "Milk"))).isTrue();

    // Verify ontology IRI was set by annotate command
    var ontologyID = actualOntology.getOntologyID();
    assertThat(ontologyID.getOntologyIRI().isPresent()).isTrue();
    assertThat(ontologyID.getOntologyIRI().get().toString())
        .isEqualTo("http://protege.stanford.edu/ontologies/groceries-dairy-subset");

    // Verify version IRI was set
    assertThat(ontologyID.getVersionIRI().isPresent()).isTrue();
    assertThat(ontologyID.getVersionIRI().get().toString())
        .isEqualTo("http://protege.stanford.edu/ontologies/groceries-dairy-subset/v1.0.0");

    // Verify IOHelper interactions
    verify(ioHelper).loadOntology(any(File.class));
    verify(ioHelper).saveOntology(any(OWLOntology.class), eq(outputPath.toString()));

    // Verify PipelineLogger was called for all stages
    verify(pipelineLogger).pipelineExecutionStarted(
        any(ProjectId.class),
        any(PipelineExecutionId.class),
        eq(pipeline));
    verify(pipelineLogger).loadingOntologyStarted(
        any(ProjectId.class),
        any(PipelineExecutionId.class),
        eq(pipeline));
    verify(pipelineLogger).loadingOntologySucceeded(
        any(ProjectId.class),
        any(PipelineExecutionId.class),
        eq(pipeline));

    // Verify each pipeline stage was logged
    for (RobotPipelineStage stage : pipeline.stages()) {
      verify(pipelineLogger).pipelineStageRunStarted(
          any(ProjectId.class),
          any(PipelineExecutionId.class),
          eq(pipeline),
          eq(stage));
      verify(pipelineLogger).pipelineStageRunFinished(
          any(ProjectId.class),
          any(PipelineExecutionId.class),
          eq(pipeline),
          eq(stage));
    }

    verify(pipelineLogger).savingOntologyStarted(
        any(ProjectId.class),
        any(PipelineExecutionId.class),
        eq(pipeline));
    verify(pipelineLogger).savingOntologySucceeded(
        any(ProjectId.class),
        any(PipelineExecutionId.class),
        eq(pipeline));
    verify(pipelineLogger).pipelineExecutionFinished(
        any(ProjectId.class),
        any(PipelineExecutionId.class),
        eq(pipeline));
  }

  /**
   * Test Case 2: Relax → Filter → Repair → Annotate
   *
   * <p>
   * This test verifies a 4-stage pipeline that:
   *
   * <ol>
   * <li>Relaxes equivalence axioms to SubClassOf relationships</li>
   * <li>Filters to keep only cookie-related classes</li>
   * <li>Repairs invalid references after filtering</li>
   * <li>Adds comprehensive metadata annotations</li>
   * </ol>
   */
  @Test
  void testExecutePipelineCase2_RelaxFilterRepairAnnotate() throws Exception {
    // Load and parse pipeline JSON
    var pipelineJson = getJsonContent("/integration/test-pipeline-case2.json");
    var pipeline = objectMapper.readValue(pipelineJson, RobotPipeline.class);

    // Verify pipeline structure
    assertThat(pipeline).isNotNull();
    assertThat(pipeline.stages()).hasSize(4);
    assertThat(pipeline.label()).isEqualTo("Test pipeline 2");

    // Verify stage labels
    assertThat(pipeline.stages().get(0).label()).isEqualTo("Relax Equivalences");
    assertThat(pipeline.stages().get(1).label()).isEqualTo("Filter Cookie Classes");
    assertThat(pipeline.stages().get(2).label()).isEqualTo("Repair Ontology");
    assertThat(pipeline.stages().get(3).label()).isEqualTo("Add Final Metadata");

    // Setup test paths - save to target/test-output for inspection
    var inputPath = getResourcePath("/integration/grocery-ontology.owl");
    var outputDir = Paths.get("target/test-output");
    Files.createDirectories(outputDir);
    var outputPath = outputDir.resolve("output-case2.owl");

    var commandState = new CommandState();

    // Create real IOHelper for actual loading/saving
    var realIOHelper = new IOHelper();

    // Mock IOHelper to delegate to real IOHelper
    when(ioHelper.loadOntology(any(File.class))).thenAnswer(invocation -> {
      File file = invocation.getArgument(0);
      return realIOHelper.loadOntology(file);
    });
    when(ioHelper.saveOntology(any(OWLOntology.class), anyString())).thenAnswer(invocation -> {
      OWLOntology ontology = invocation.getArgument(0);
      String savePath = invocation.getArgument(1);
      return realIOHelper.saveOntology(ontology, savePath);
    });

    // Mock Provider to return fresh CommandState
    when(commandStateProvider.get()).thenReturn(commandState);

    // Execute pipeline (use projectId from JSON)
    var result = executor.executePipeline(
        pipeline.projectId(),
        inputPath,
        outputPath,
        pipeline);

    // Verify results
    assertThat(result).isNotNull();
    var actualOntology = result.getOntology();
    assertThat(actualOntology).isNotNull();

    // Verify output was saved to file
    assertThat(outputPath).exists();

    // Verify expected cookie classes were retained after filtering
    var ontologyIRI = IRI.create("http://protege.stanford.edu/ontologies/groceries/");
    assertThat(actualOntology.containsClassInSignature(IRI.create(ontologyIRI + "Cookie"))).isTrue();
    assertThat(actualOntology.containsClassInSignature(IRI.create(ontologyIRI + "Shortbread"))).isTrue();

    // Verify ontology IRI was set by annotate command
    var ontologyID = actualOntology.getOntologyID();
    assertThat(ontologyID.getOntologyIRI().isPresent()).isTrue();
    assertThat(ontologyID.getOntologyIRI().get().toString())
        .isEqualTo("http://protege.stanford.edu/ontologies/groceries-cookies-subset");

    // Verify version IRI was set
    assertThat(ontologyID.getVersionIRI().isPresent()).isTrue();
    assertThat(ontologyID.getVersionIRI().get().toString())
        .isEqualTo("http://protege.stanford.edu/ontologies/groceries-cookies-subset/v2.0.0");

    // Verify IOHelper interactions
    verify(ioHelper).loadOntology(any(File.class));
    verify(ioHelper).saveOntology(any(OWLOntology.class), eq(outputPath.toString()));

    // Verify PipelineLogger was called for pipeline lifecycle
    verify(pipelineLogger).pipelineExecutionStarted(
        any(ProjectId.class),
        any(PipelineExecutionId.class),
        eq(pipeline));
    verify(pipelineLogger).pipelineExecutionFinished(
        any(ProjectId.class),
        any(PipelineExecutionId.class),
        eq(pipeline));

    // Verify ontology loading was logged
    verify(pipelineLogger).loadingOntologyStarted(
        any(ProjectId.class),
        any(PipelineExecutionId.class),
        eq(pipeline));
    verify(pipelineLogger).loadingOntologySucceeded(
        any(ProjectId.class),
        any(PipelineExecutionId.class),
        eq(pipeline));

    // Verify all 5 stages were logged (start and finish for each)
    verify(pipelineLogger, atLeastOnce()).pipelineStageRunStarted(
        any(ProjectId.class),
        any(PipelineExecutionId.class),
        eq(pipeline),
        any(RobotPipelineStage.class));
    verify(pipelineLogger, atLeastOnce()).pipelineStageRunFinished(
        any(ProjectId.class),
        any(PipelineExecutionId.class),
        eq(pipeline),
        any(RobotPipelineStage.class));

    // Verify ontology saving was logged
    verify(pipelineLogger).savingOntologyStarted(
        any(ProjectId.class),
        any(PipelineExecutionId.class),
        eq(pipeline));
    verify(pipelineLogger).savingOntologySucceeded(
        any(ProjectId.class),
        any(PipelineExecutionId.class),
        eq(pipeline));
  }

  /**
   * Helper method to load JSON content from test resources.
   */
  private String getJsonContent(String path) throws IOException {
    return new String(
        Objects.requireNonNull(getClass().getResourceAsStream(path)).readAllBytes(),
        StandardCharsets.UTF_8);
  }

  /**
   * Helper method to get absolute path to a test resource file.
   */
  private Path getResourcePath(String resourcePath) throws IOException {
    var resource = getClass().getResource(resourcePath);
    if (resource == null) {
      throw new IOException("Resource not found: " + resourcePath);
    }
    return Paths.get(resource.getPath());
  }
}
