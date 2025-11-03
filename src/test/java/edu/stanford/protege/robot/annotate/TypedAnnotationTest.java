package edu.stanford.protege.robot.annotate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TypedAnnotationTest {

  @Test
  void shouldReturnCorrectArgName() {
    var annotation = new TypedAnnotation("ex:count", "42", "xsd:integer");

    assertThat(annotation.getArgName()).isEqualTo("--typed-annotation");
  }

  @Test
  void shouldReturnCorrectProperty() {
    var annotation = new TypedAnnotation("ex:price", "19.99", "xsd:decimal");

    assertThat(annotation.property()).isEqualTo("ex:price");
  }

  @Test
  void shouldReturnCorrectValue() {
    var annotation = new TypedAnnotation("ex:date", "2023-12-01", "xsd:date");

    assertThat(annotation.value()).isEqualTo("2023-12-01");
  }

  @Test
  void shouldReturnCorrectType() {
    var annotation = new TypedAnnotation("ex:count", "100", "xsd:integer");

    assertThat(annotation.type()).isEqualTo("xsd:integer");
  }

  @Test
  void shouldGenerateCorrectArgs() {
    var annotation = new TypedAnnotation("ex:score", "95.5", "xsd:double");

    assertThat(annotation.getArgs())
        .containsExactly("--typed-annotation", "ex:score", "95.5", "xsd:double");
  }

  @Test
  void shouldGenerateArgsWithFullTypeIRI() {
    var annotation = new TypedAnnotation(
        "ex:timestamp", "2023-12-01T10:00:00", "http://www.w3.org/2001/XMLSchema#dateTime");

    assertThat(annotation.getArgs())
        .containsExactly(
            "--typed-annotation",
            "ex:timestamp",
            "2023-12-01T10:00:00",
            "http://www.w3.org/2001/XMLSchema#dateTime");
  }

  @Test
  void shouldHandleIntegerType() {
    var annotation = new TypedAnnotation("ex:age", "25", "xsd:integer");

    assertThat(annotation.type()).isEqualTo("xsd:integer");
    assertThat(annotation.getArgs()).containsExactly("--typed-annotation", "ex:age", "25", "xsd:integer");
  }

  @Test
  void shouldHandleBooleanType() {
    var annotation = new TypedAnnotation("ex:active", "true", "xsd:boolean");

    assertThat(annotation.getArgs())
        .containsExactly("--typed-annotation", "ex:active", "true", "xsd:boolean");
  }
}
