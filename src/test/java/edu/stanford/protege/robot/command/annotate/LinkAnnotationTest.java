package edu.stanford.protege.robot.command.annotate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LinkAnnotationTest {

  @Test
  void shouldReturnCorrectArgName() {
    var annotation = new LinkAnnotation("dc:license", "http://example.org/license");

    assertThat(annotation.getArgName()).isEqualTo("--link-annotation");
  }

  @Test
  void shouldReturnCorrectProperty() {
    var annotation = new LinkAnnotation("rdfs:seeAlso", "http://example.org/resource");

    assertThat(annotation.property()).isEqualTo("rdfs:seeAlso");
  }

  @Test
  void shouldReturnCorrectValue() {
    var annotation = new LinkAnnotation("dc:source", "http://example.org/source");

    assertThat(annotation.value()).isEqualTo("http://example.org/source");
  }

  @Test
  void shouldGenerateCorrectArgs() {
    var annotation = new LinkAnnotation("dc:license", "http://creativecommons.org/licenses/by/4.0/");

    assertThat(annotation.getArgs())
        .containsExactly(
            "--link-annotation", "dc:license", "http://creativecommons.org/licenses/by/4.0/");
  }

  @Test
  void shouldHandleHttpsUrls() {
    var annotation = new LinkAnnotation("rdfs:isDefinedBy", "https://example.org/ontology");

    assertThat(annotation.getArgs())
        .containsExactly("--link-annotation", "rdfs:isDefinedBy", "https://example.org/ontology");
  }

  @Test
  void shouldHandleRelativeUrls() {
    var annotation = new LinkAnnotation("rdfs:seeAlso", "/relative/path");

    assertThat(annotation.getArgs())
        .containsExactly("--link-annotation", "rdfs:seeAlso", "/relative/path");
  }

  @Test
  void shouldHandleUrnUrls() {
    var annotation = new LinkAnnotation("dc:identifier", "urn:isbn:0451450523");

    assertThat(annotation.getArgs())
        .containsExactly("--link-annotation", "dc:identifier", "urn:isbn:0451450523");
  }
}
