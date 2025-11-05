package edu.stanford.protege.robot.command.annotate;

import static org.assertj.core.api.Assertions.assertThat;

import edu.stanford.protege.robot.command.annotate.PlainAnnotation;
import org.junit.jupiter.api.Test;

class PlainAnnotationTest {

  @Test
  void shouldReturnCorrectArgName() {
    var annotation = new PlainAnnotation("rdfs:comment", "Test comment");

    assertThat(annotation.getArgName()).isEqualTo("--annotation");
  }

  @Test
  void shouldReturnCorrectProperty() {
    var annotation = new PlainAnnotation("rdfs:label", "Test label");

    assertThat(annotation.property()).isEqualTo("rdfs:label");
  }

  @Test
  void shouldReturnCorrectValue() {
    var annotation = new PlainAnnotation("dc:title", "Example Title");

    assertThat(annotation.value()).isEqualTo("Example Title");
  }

  @Test
  void shouldGenerateCorrectArgs() {
    var annotation = new PlainAnnotation("rdfs:comment", "This is a comment");

    assertThat(annotation.getArgs())
        .containsExactly("--annotation", "rdfs:comment", "This is a comment");
  }

  @Test
  void shouldGenerateArgsWithFullIRI() {
    var annotation = new PlainAnnotation("http://purl.org/dc/terms/description", "Full description");

    assertThat(annotation.getArgs())
        .containsExactly(
            "--annotation", "http://purl.org/dc/terms/description", "Full description");
  }

  @Test
  void shouldHandleEmptyValue() {
    var annotation = new PlainAnnotation("rdfs:comment", "");

    assertThat(annotation.getArgs()).containsExactly("--annotation", "rdfs:comment", "");
  }

  @Test
  void shouldHandleSpecialCharactersInValue() {
    var annotation = new PlainAnnotation("rdfs:comment", "Value with \"quotes\" and newlines\n");

    assertThat(annotation.value()).isEqualTo("Value with \"quotes\" and newlines\n");
  }
}
