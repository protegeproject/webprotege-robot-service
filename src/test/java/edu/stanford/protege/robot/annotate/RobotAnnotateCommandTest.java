package edu.stanford.protege.robot.annotate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.obolibrary.robot.AnnotateCommand;
import org.semanticweb.owlapi.model.IRI;

class RobotAnnotateCommandTest {

  @Test
  void shouldReturnAnnotateCommand() {
    var command = new RobotAnnotateCommand(
        null, null, List.of(new PlainAnnotation("rdfs:label", "Test")));

    assertThat(command.getCommand()).isInstanceOf(AnnotateCommand.class);
  }

  @Test
  void shouldGenerateArgsWithOntologyIriOnly() {
    var ontologyIri = IRI.create("http://example.org/ontology");
    var command = new RobotAnnotateCommand(ontologyIri, null, List.of());

    assertThat(command.getArgs())
        .contains("--ontology-iri", "http://example.org/ontology");
  }

  @Test
  void shouldGenerateArgsWithVersionIriOnly() {
    var versionIri = IRI.create("http://example.org/ontology/v1.0");
    var command = new RobotAnnotateCommand(null, versionIri, List.of());

    assertThat(command.getArgs())
        .contains("--version-iri", "http://example.org/ontology/v1.0");
  }

  @Test
  void shouldGenerateArgsWithBothIris() {
    var ontologyIri = IRI.create("http://example.org/ontology");
    var versionIri = IRI.create("http://example.org/ontology/v1.0");
    var command = new RobotAnnotateCommand(ontologyIri, versionIri, List.of());

    var args = command.getArgs();
    assertThat(args)
        .contains("--ontology-iri", "http://example.org/ontology")
        .contains("--version-iri", "http://example.org/ontology/v1.0");
  }

  @Test
  void shouldNotIncludeIriArgsWhenNull() {
    var command = new RobotAnnotateCommand(null, null, List.of());

    var args = command.getArgs();
    assertThat(args).doesNotContain("--ontology-iri", "--version-iri");
  }

  @Test
  void shouldGenerateArgsWithSinglePlainAnnotation() {
    var annotation = new PlainAnnotation("rdfs:label", "Example Ontology");
    var command = new RobotAnnotateCommand(null, null, List.of(annotation));

    assertThat(command.getArgs())
        .containsSequence("--annotation", "rdfs:label", "Example Ontology");
  }

  @Test
  void shouldGenerateArgsWithMultipleAnnotations() {
    var annotations = List.<Annotation>of(
        new PlainAnnotation("rdfs:label", "Example"),
        new PlainAnnotation("dc:description", "Test ontology"));
    var command = new RobotAnnotateCommand(null, null, annotations);

    var args = command.getArgs();
    assertThat(args)
        .containsSequence("--annotation", "rdfs:label", "Example")
        .containsSequence("--annotation", "dc:description", "Test ontology");
  }

  @Test
  void shouldGenerateArgsWithMixedAnnotationTypes() {
    var annotations = List.<Annotation>of(
        new PlainAnnotation("rdfs:label", "Example"),
        new LanguageAnnotation("dc:title", "Exemple", "fr"),
        new TypedAnnotation("ex:count", "42", "xsd:integer"),
        new LinkAnnotation("dc:license", "http://example.org/license"));
    var command = new RobotAnnotateCommand(null, null, annotations);

    var args = command.getArgs();
    assertThat(args)
        .containsSequence("--annotation", "rdfs:label", "Example")
        .containsSequence("--language-annotation", "dc:title", "Exemple", "fr")
        .containsSequence("--typed-annotation", "ex:count", "42", "xsd:integer")
        .containsSequence("--link-annotation", "dc:license", "http://example.org/license");
  }

  @Test
  void shouldGenerateArgsWithInterpolateFlag() {
    var command = new RobotAnnotateCommand(null, null, List.of(), AnnotateFlags.INTERPOLATE);

    assertThat(command.getArgs()).contains("--interpolate", "true");
  }

  @Test
  void shouldGenerateArgsWithAnnotateDerivedFromFlag() {
    var command = new RobotAnnotateCommand(null, null, List.of(), AnnotateFlags.ANNOTATE_DERIVED_FROM);

    assertThat(command.getArgs()).contains("--annotate-derived-from", "true");
  }

  @Test
  void shouldGenerateArgsWithAnnotateDefinedByFlag() {
    var command = new RobotAnnotateCommand(null, null, List.of(), AnnotateFlags.ANNOTATE_DEFINED_BY);

    assertThat(command.getArgs()).contains("--annotate-defined-by", "true");
  }

  @Test
  void shouldGenerateArgsWithRemoveAnnotationsFlag() {
    var command = new RobotAnnotateCommand(null, null, List.of(), AnnotateFlags.REMOVE_ANNOTATIONS);

    assertThat(command.getArgs()).contains("--remove-annotations", "true");
  }

  @Test
  void shouldGenerateArgsWithMultipleFlags() {
    var command = new RobotAnnotateCommand(
        null,
        null,
        List.of(),
        AnnotateFlags.INTERPOLATE,
        AnnotateFlags.ANNOTATE_DEFINED_BY);

    var args = command.getArgs();
    assertThat(args).contains("--interpolate", "true").contains("--annotate-defined-by", "true");
  }

  @Test
  void shouldNotIncludeFlagsWhenNoneProvided() {
    var command = new RobotAnnotateCommand(null, null, List.of());

    var args = command.getArgs();
    assertThat(args)
        .doesNotContain("--interpolate")
        .doesNotContain("--annotate-derived-from")
        .doesNotContain("--annotate-defined-by")
        .doesNotContain("--remove-annotations");
  }

  @Test
  void shouldGenerateCompleteArgsWithAllParameters() {
    var ontologyIri = IRI.create("http://example.org/ontology");
    var versionIri = IRI.create("http://example.org/ontology/v1.0");
    var annotations = List.<Annotation>of(
        new PlainAnnotation("rdfs:label", "Example Ontology"),
        new LanguageAnnotation("dc:title", "Exemple", "fr"));
    var command = new RobotAnnotateCommand(
        ontologyIri, versionIri, annotations, AnnotateFlags.INTERPOLATE);

    var args = command.getArgs();
    assertThat(args)
        .contains("--ontology-iri", "http://example.org/ontology")
        .contains("--version-iri", "http://example.org/ontology/v1.0")
        .contains("--interpolate", "true")
        .containsSequence("--annotation", "rdfs:label", "Example Ontology")
        .containsSequence("--language-annotation", "dc:title", "Exemple", "fr");
  }

  @Test
  void shouldHandleEmptyAnnotationsList() {
    var command = new RobotAnnotateCommand(null, null, List.of());

    assertThat(command.getArgs()).isNotNull().isEmpty();
  }

  @Test
  void shouldGenerateArgsArrayFromArgsList() {
    var annotation = new PlainAnnotation("rdfs:label", "Test");
    var command = new RobotAnnotateCommand(null, null, List.of(annotation));

    var argsArray = command.getArgsArray();
    assertThat(argsArray).containsExactly("--annotation", "rdfs:label", "Test");
  }

  @Test
  void shouldPreserveAnnotationOrder() {
    var annotations = List.<Annotation>of(
        new PlainAnnotation("rdfs:label", "First"),
        new PlainAnnotation("dc:title", "Second"),
        new PlainAnnotation("dc:description", "Third"));
    var command = new RobotAnnotateCommand(null, null, annotations);

    var args = command.getArgs();
    int labelIndex = args.indexOf("First");
    int titleIndex = args.indexOf("Second");
    int descIndex = args.indexOf("Third");

    assertThat(labelIndex).isLessThan(titleIndex);
    assertThat(titleIndex).isLessThan(descIndex);
  }
}
