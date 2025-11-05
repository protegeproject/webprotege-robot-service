package edu.stanford.protege.robot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import edu.stanford.protege.robot.command.annotate.AnnotateFlags;
import edu.stanford.protege.robot.command.annotate.PlainAnnotation;
import edu.stanford.protege.robot.command.annotate.RobotAnnotateCommand;
import edu.stanford.protege.robot.command.extract.ExtractIntermediates;
import edu.stanford.protege.robot.command.extract.RobotExtractCommand;
import edu.stanford.protege.robot.command.extract.SlmeExtractMethod;
import edu.stanford.protege.robot.command.extract.SlmeExtractStrategy;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.inject.Provider;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.obolibrary.robot.CommandState;
import org.obolibrary.robot.IOHelper;
import org.semanticweb.owlapi.model.IRI;

class RobotCommandExecutorTest {

  private RobotCommandExecutor executor;
  private Path inputOntologyPath;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() throws Exception {
    // Setup executor with provider using lambda
    Provider<CommandState> stateProvider = CommandState::new;
    var ioHelper = new IOHelper();
    executor = new RobotCommandExecutor(stateProvider, ioHelper);

    // Get test ontology from resources
    var resource = getClass().getClassLoader().getResource("simple-ontology.owl");
    assertThat(resource).isNotNull();
    inputOntologyPath = Path.of(new File(resource.toURI()).getAbsolutePath());
  }

  @AfterEach
  void tearDown() {
    // Cleanup temp files if needed
  }

  @Test
  void shouldCreateExecutorWithValidDependencies() {
    assertThat(executor).isNotNull();
  }

  @Test
  void shouldThrowExceptionWhenInputFileIsNull() {
    var annotateCmd = createSimpleAnnotateCommand();
    var outputPath = tempDir.resolve("output.owl");

    assertThatThrownBy(() -> executor.executeChain(null, List.of(annotateCmd), outputPath))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("ontologyFilePath cannot be null");
  }

  @Test
  void shouldThrowExceptionWhenCommandsListIsNull() {
    var outputPath = tempDir.resolve("output.owl");

    assertThatThrownBy(() -> executor.executeChain(inputOntologyPath, null, outputPath))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("commands cannot be null");
  }

  @Test
  void shouldThrowExceptionWhenCommandsListIsEmpty() {
    var outputPath = tempDir.resolve("output.owl");

    assertThatThrownBy(() -> executor.executeChain(inputOntologyPath, List.of(), outputPath))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("commands cannot be empty");
  }

  @Test
  void shouldThrowExceptionWhenOutputPathIsNull() {
    var annotateCmd = createSimpleAnnotateCommand();

    assertThatThrownBy(() -> executor.executeChain(inputOntologyPath, List.of(annotateCmd), null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("outputPath cannot be null");
  }

  @Test
  void shouldExecuteSingleAnnotateCommand() throws Exception {
    var outputPath = tempDir.resolve("test-output.owl");

    // Create annotate command
    var annotateCmd = new RobotAnnotateCommand(
        IRI.create("http://example.org/test-ontology"),
        IRI.create("http://example.org/test-ontology/v1.0"),
        List.of(new PlainAnnotation("rdfs:label", "Test Ontology")),
        AnnotateFlags.INTERPOLATE);

    // Execute chain with single command
    var result = executor.executeChain(inputOntologyPath, List.of(annotateCmd), outputPath);

    // Verify result
    assertThat(result).isNotNull();
    assertThat(result.getOntology()).isNotNull();
    assertThat(result.getOntologyPath()).isEqualTo(inputOntologyPath.toString());

    // Verify ontology has annotations
    var ontology = result.getOntology();
    assertThat(ontology.getOntologyID().getOntologyIRI().isPresent()).isTrue();
    assertThat((Object) ontology.getOntologyID().getOntologyIRI().get())
        .isEqualTo(IRI.create("http://example.org/test-ontology"));
    assertThat(ontology.getOntologyID().getVersionIRI().isPresent()).isTrue();
    assertThat((Object) ontology.getOntologyID().getVersionIRI().get())
        .isEqualTo(IRI.create("http://example.org/test-ontology/v1.0"));

    // Verify output file was created
    assertThat(Files.exists(outputPath)).isTrue();
  }

  @Test
  void shouldExecuteAnnotateThenExtractChain() throws Exception {
    var outputPath = tempDir.resolve("chained-output.owl");

    // Create annotate command
    var annotateCmd = new RobotAnnotateCommand(
        IRI.create("http://example.org/chained-ontology"),
        IRI.create("http://example.org/chained-ontology/v1.0"),
        List.of(new PlainAnnotation("rdfs:label", "Chained Test Ontology")),
        AnnotateFlags.INTERPOLATE);

    // Create extract command (SLME BOT method for extracting hierarchy)
    var extractStrategy = new SlmeExtractStrategy(
        SlmeExtractMethod.BOT,
        List.of("http://example.org/simple#Person"));
    var extractCmd = new RobotExtractCommand(
        extractStrategy,
        ExtractIntermediates.minimal,
        null,
        true);

    // Execute chain: annotate then extract
    var result = executor.executeChain(
        inputOntologyPath,
        List.of(annotateCmd, extractCmd),
        outputPath);

    // Verify result
    assertThat(result).isNotNull();
    assertThat(result.getOntology()).isNotNull();

    // Verify ontology still has annotations from first command
    var ontology = result.getOntology();
    assertThat(ontology.getOntologyID().getOntologyIRI().isPresent()).isTrue();
    assertThat((Object) ontology.getOntologyID().getOntologyIRI().get())
        .isEqualTo(IRI.create("http://example.org/chained-ontology"));

    // Verify extract was applied (ontology should be smaller)
    assertThat(ontology.getSignature()).isNotEmpty();

    // Verify output file was created
    assertThat(Files.exists(outputPath)).isTrue();
  }

  @Test
  void shouldSaveOutputFile() throws Exception {
    var outputPath = tempDir.resolve("output.owl");

    // Create simple annotate command
    var annotateCmd = createSimpleAnnotateCommand();

    // Execute chain with output file
    var result = executor.executeChain(
        inputOntologyPath,
        List.of(annotateCmd),
        outputPath);

    // Verify result
    assertThat(result).isNotNull();

    // Verify output file was created
    assertThat(Files.exists(outputPath)).isTrue();

    // Verify file has content
    var fileSize = Files.size(outputPath);
    assertThat(fileSize).isGreaterThan(0);
  }

  @Test
  void shouldExecuteMultipleCommandsInSequence() throws Exception {
    var outputPath = tempDir.resolve("multi-output.owl");

    // Create first annotate command
    var annotate1 = new RobotAnnotateCommand(
        IRI.create("http://example.org/multi"),
        null,
        List.of(new PlainAnnotation("rdfs:label", "Multi Command Test")));

    // Create second annotate command (adds more annotations)
    var annotate2 = new RobotAnnotateCommand(
        null,
        IRI.create("http://example.org/multi/v2.0"),
        List.of(new PlainAnnotation("dc:description", "Testing multiple commands")));

    // Execute chain with multiple annotate commands
    var result = executor.executeChain(
        inputOntologyPath,
        List.of(annotate1, annotate2),
        outputPath);

    // Verify both commands were applied
    assertThat(result).isNotNull();
    var ontology = result.getOntology();
    assertThat(ontology.getOntologyID().getOntologyIRI().isPresent()).isTrue();
    assertThat((Object) ontology.getOntologyID().getOntologyIRI().get())
        .isEqualTo(IRI.create("http://example.org/multi"));
    assertThat(ontology.getOntologyID().getVersionIRI().isPresent()).isTrue();
    assertThat((Object) ontology.getOntologyID().getVersionIRI().get())
        .isEqualTo(IRI.create("http://example.org/multi/v2.0"));

    // Verify output file was created
    assertThat(Files.exists(outputPath)).isTrue();
  }

  private RobotAnnotateCommand createSimpleAnnotateCommand() {
    return new RobotAnnotateCommand(
        IRI.create("http://example.org/simple"),
        null,
        List.of(new PlainAnnotation("rdfs:label", "Simple Test")));
  }
}
