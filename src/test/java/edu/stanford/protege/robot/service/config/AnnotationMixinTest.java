package edu.stanford.protege.robot.service.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.robot.annotate.Annotation;
import edu.stanford.protege.robot.annotate.LanguageAnnotation;
import edu.stanford.protege.robot.annotate.LinkAnnotation;
import edu.stanford.protege.robot.annotate.PlainAnnotation;
import edu.stanford.protege.robot.annotate.TypedAnnotation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;

/**
 * Unit tests for Annotation polymorphic JSON serialization and deserialization.
 *
 * <p>
 * Tests verify that all Annotation implementations can be correctly serialized to JSON with type
 * discriminators and deserialized back to the correct concrete classes.
 */
@JsonTest
@Import(JacksonConfiguration.class)
class AnnotationMixinTest {

  @Autowired
  private ObjectMapper objectMapper;

  /**
   * Tests PlainAnnotation serialization with type discriminator.
   */
  @Test
  void testPlainAnnotationSerialization() throws Exception {
    PlainAnnotation annotation = new PlainAnnotation("rdfs:label", "Example Ontology");
    String json = objectMapper.writeValueAsString(annotation);

    assertNotNull(json);
    // Should contain type discriminator and all fields
    assertTrue(json.contains("\"@type\":\"PlainAnnotation\""));
    assertTrue(json.contains("\"property\":\"rdfs:label\""));
    assertTrue(json.contains("\"value\":\"Example Ontology\""));
  }

  /**
   * Tests PlainAnnotation deserialization from JSON with type discriminator.
   */
  @Test
  void testPlainAnnotationDeserialization() throws Exception {
    String json = "{\"@type\":\"PlainAnnotation\",\"property\":\"rdfs:label\",\"value\":\"Test\"}";
    Annotation annotation = objectMapper.readValue(json, Annotation.class);

    assertNotNull(annotation);
    assertInstanceOf(PlainAnnotation.class, annotation);
    assertEquals("rdfs:label", annotation.property());
    assertEquals("Test", annotation.value());
  }

  /**
   * Tests TypedAnnotation serialization with type discriminator.
   */
  @Test
  void testTypedAnnotationSerialization() throws Exception {
    TypedAnnotation annotation = new TypedAnnotation("dc:date", "2025-01-01", "xsd:date");
    String json = objectMapper.writeValueAsString(annotation);

    assertNotNull(json);
    assertTrue(json.contains("\"@type\":\"TypedAnnotation\""));
    assertTrue(json.contains("\"property\":\"dc:date\""));
    assertTrue(json.contains("\"value\":\"2025-01-01\""));
    assertTrue(json.contains("\"type\":\"xsd:date\"") || json.contains("\"datatype\":\"xsd:date\""));
  }

  /**
   * Tests TypedAnnotation deserialization from JSON with type discriminator.
   */
  @Test
  void testTypedAnnotationDeserialization() throws Exception {
    String json = "{\"@type\":\"TypedAnnotation\",\"property\":\"dc:date\",\"value\":\"2025-01-01\",\"type\":\"xsd:date\"}";
    Annotation annotation = objectMapper.readValue(json, Annotation.class);

    assertNotNull(annotation);
    assertInstanceOf(TypedAnnotation.class, annotation);
    TypedAnnotation typed = (TypedAnnotation) annotation;
    assertEquals("dc:date", typed.property());
    assertEquals("2025-01-01", typed.value());
    assertEquals("xsd:date", typed.type());
  }

  /**
   * Tests LanguageAnnotation serialization with type discriminator.
   */
  @Test
  void testLanguageAnnotationSerialization() throws Exception {
    LanguageAnnotation annotation = new LanguageAnnotation("dc:title", "Mon Ontologie", "fr");
    String json = objectMapper.writeValueAsString(annotation);

    assertNotNull(json);
    assertTrue(json.contains("\"@type\":\"LanguageAnnotation\""));
    assertTrue(json.contains("\"property\":\"dc:title\""));
    assertTrue(json.contains("\"value\":\"Mon Ontologie\""));
    assertTrue(json.contains("\"lang\":\"fr\""));
  }

  /**
   * Tests LanguageAnnotation deserialization from JSON with type discriminator.
   */
  @Test
  void testLanguageAnnotationDeserialization() throws Exception {
    String json = "{\"@type\":\"LanguageAnnotation\",\"property\":\"dc:title\",\"value\":\"Mon Ontologie\",\"lang\":\"fr\"}";
    Annotation annotation = objectMapper.readValue(json, Annotation.class);

    assertNotNull(annotation);
    assertInstanceOf(LanguageAnnotation.class, annotation);
    LanguageAnnotation lang = (LanguageAnnotation) annotation;
    assertEquals("dc:title", lang.property());
    assertEquals("Mon Ontologie", lang.value());
    assertEquals("fr", lang.lang());
  }

  /**
   * Tests LinkAnnotation serialization with type discriminator.
   */
  @Test
  void testLinkAnnotationSerialization() throws Exception {
    LinkAnnotation annotation = new LinkAnnotation("dc:license", "https://creativecommons.org/licenses/by/4.0/");
    String json = objectMapper.writeValueAsString(annotation);

    assertNotNull(json);
    assertTrue(json.contains("\"@type\":\"LinkAnnotation\""));
    assertTrue(json.contains("\"property\":\"dc:license\""));
    assertTrue(json.contains("\"value\":\"https://creativecommons.org/licenses/by/4.0/\""));
  }

  /**
   * Tests LinkAnnotation deserialization from JSON with type discriminator.
   */
  @Test
  void testLinkAnnotationDeserialization() throws Exception {
    String json = "{\"@type\":\"LinkAnnotation\",\"property\":\"dc:license\",\"value\":\"https://creativecommons.org/licenses/by/4.0/\"}";
    Annotation annotation = objectMapper.readValue(json, Annotation.class);

    assertNotNull(annotation);
    assertInstanceOf(LinkAnnotation.class, annotation);
    assertEquals("dc:license", annotation.property());
    assertEquals("https://creativecommons.org/licenses/by/4.0/", annotation.value());
  }

  /**
   * Tests round-trip serialization for all annotation types.
   */
  @Test
  void testAnnotationRoundTrip() throws Exception {
    Annotation[] annotations = {new PlainAnnotation("rdfs:label", "Test"),
        new TypedAnnotation("dc:date", "2025-01-01", "xsd:date"),
        new LanguageAnnotation("dc:title", "Test", "en"),
        new LinkAnnotation("dc:license", "http://example.org/license")};

    for (Annotation original : annotations) {
      String json = objectMapper.writeValueAsString(original);
      Annotation deserialized = objectMapper.readValue(json, Annotation.class);

      assertNotNull(deserialized);
      assertEquals(original.getClass(), deserialized.getClass());
      assertEquals(original.property(), deserialized.property());
      assertEquals(original.value(), deserialized.value());
    }
  }
}
