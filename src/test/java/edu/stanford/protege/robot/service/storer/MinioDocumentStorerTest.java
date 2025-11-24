package edu.stanford.protege.robot.service.storer;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MinioDocumentStorerTest {

  private Method determineContentTypeMethod;

  @BeforeEach
  void setUp() throws NoSuchMethodException {
    // Access the private static method for testing
    determineContentTypeMethod = MinioDocumentStorer.class.getDeclaredMethod("determineContentType", String.class);
    determineContentTypeMethod.setAccessible(true);
  }

  private String invokeContentType(String filePath) throws Exception {
    return (String) determineContentTypeMethod.invoke(null, filePath);
  }

  // ROBOT convert format tests (7 supported formats)
  @Test
  void shouldReturnJsonForJsonExtension() throws Exception {
    assertThat(invokeContentType("ontology.json")).isEqualTo("application/json");
  }

  @Test
  void shouldReturnOboForOboExtension() throws Exception {
    assertThat(invokeContentType("ontology.obo")).isEqualTo("text/obo");
  }

  @Test
  void shouldReturnOwlFunctionalForOfnExtension() throws Exception {
    assertThat(invokeContentType("ontology.ofn")).isEqualTo("text/owl-functional");
  }

  @Test
  void shouldReturnOwlManchesterForOmnExtension() throws Exception {
    assertThat(invokeContentType("ontology.omn")).isEqualTo("text/owl-manchester");
  }

  @Test
  void shouldReturnRdfXmlForOwlExtension() throws Exception {
    assertThat(invokeContentType("ontology.owl")).isEqualTo("application/rdf+xml");
  }

  @Test
  void shouldReturnOwlXmlForOwxExtension() throws Exception {
    assertThat(invokeContentType("ontology.owx")).isEqualTo("application/owl+xml");
  }

  @Test
  void shouldReturnTurtleForTtlExtension() throws Exception {
    assertThat(invokeContentType("ontology.ttl")).isEqualTo("text/turtle");
  }

  // Case insensitivity tests
  @ParameterizedTest
  @CsvSource({
      "file.JSON,application/json",
      "file.Json,application/json",
      "file.OBO,text/obo",
      "file.Obo,text/obo",
      "file.OWL,application/rdf+xml",
      "file.Owl,application/rdf+xml",
      "file.TTL,text/turtle",
      "file.Ttl,text/turtle"
  })
  void shouldBeCaseInsensitive(String filePath, String expectedContentType) throws Exception {
    assertThat(invokeContentType(filePath)).isEqualTo(expectedContentType);
  }

  // Full path tests
  @Test
  void shouldHandleFullPathWithDirectories() throws Exception {
    assertThat(invokeContentType("/path/to/ontology/file.owl")).isEqualTo("application/rdf+xml");
  }

  @Test
  void shouldHandlePathWithMultipleDots() throws Exception {
    assertThat(invokeContentType("my.ontology.file.ttl")).isEqualTo("text/turtle");
  }

  @Test
  void shouldHandleWindowsStylePath() throws Exception {
    assertThat(invokeContentType("C:\\Users\\Documents\\ontology.obo")).isEqualTo("text/obo");
  }

  // Edge case tests
  @Test
  void shouldReturnDefaultForNullPath() throws Exception {
    assertThat(invokeContentType(null)).isEqualTo("application/octet-stream");
  }

  @Test
  void shouldReturnDefaultForEmptyPath() throws Exception {
    assertThat(invokeContentType("")).isEqualTo("application/octet-stream");
  }

  @Test
  void shouldReturnDefaultForUnknownExtension() throws Exception {
    assertThat(invokeContentType("file.xyz")).isEqualTo("application/octet-stream");
  }

  @Test
  void shouldReturnDefaultForNoExtension() throws Exception {
    assertThat(invokeContentType("README")).isEqualTo("application/octet-stream");
  }

  @Test
  void shouldReturnDefaultForDotFileWithoutExtension() throws Exception {
    assertThat(invokeContentType(".gitignore")).isEqualTo("application/octet-stream");
  }

  // Unsupported format tests (formats not in ROBOT convert)
  @Test
  void shouldReturnDefaultForRdfExtension() throws Exception {
    assertThat(invokeContentType("ontology.rdf")).isEqualTo("application/octet-stream");
  }

  @Test
  void shouldReturnDefaultForCsvExtension() throws Exception {
    assertThat(invokeContentType("export.csv")).isEqualTo("application/octet-stream");
  }

  @Test
  void shouldReturnDefaultForTsvExtension() throws Exception {
    assertThat(invokeContentType("export.tsv")).isEqualTo("application/octet-stream");
  }

  @Test
  void shouldReturnDefaultForHtmlExtension() throws Exception {
    assertThat(invokeContentType("export.html")).isEqualTo("application/octet-stream");
  }

  @Test
  void shouldReturnDefaultForXlsxExtension() throws Exception {
    assertThat(invokeContentType("export.xlsx")).isEqualTo("application/octet-stream");
  }

  // Real-world path examples
  @Test
  void shouldHandleTypicalMinioPath() throws Exception {
    assertThat(invokeContentType("/tmp/robot-output-12345678.owl"))
        .isEqualTo("application/rdf+xml");
  }

  @Test
  void shouldHandleConvertedFileWithTimestamp() throws Exception {
    assertThat(invokeContentType("ontology-2024-11-24-15-30-00.json"))
        .isEqualTo("application/json");
  }

  @Test
  void shouldHandleUuidInFileName() throws Exception {
    assertThat(invokeContentType("robot-output-550e8400-e29b-41d4-a716-446655440000.json"))
        .isEqualTo("application/json");
  }
}
