package edu.stanford.protege.robot.service.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;

/**
 * Unit tests for IRI JSON serialization and deserialization.
 *
 * <p>
 * Tests verify that OWL API IRI objects can be correctly serialized to JSON strings and
 * deserialized back to IRI instances using the custom Jackson component.
 */
@JsonTest
@Import(JacksonConfiguration.class)
class IriSerializeDeserializeHandlerTest {

  @Autowired
  private ObjectMapper objectMapper;

  private IRI testIri;

  @BeforeEach
  void setUp() {
    testIri = IRI.create("http://example.org/ontology");
  }

  /**
   * Tests that IRI serializes to a JSON string.
   */
  @Test
  void testIriSerialization() throws Exception {
    String json = objectMapper.writeValueAsString(testIri);

    assertNotNull(json);
    assertEquals("\"http://example.org/ontology\"", json);
  }

  /**
   * Tests that JSON string deserializes to IRI.
   */
  @Test
  void testIriDeserialization() throws Exception {
    String json = "\"http://example.org/ontology\"";
    IRI deserialized = objectMapper.readValue(json, IRI.class);

    assertNotNull(deserialized);
    assertEquals(testIri, deserialized);
  }

  /**
   * Tests round-trip serialization: IRI → JSON → IRI.
   */
  @Test
  void testIriRoundTrip() throws Exception {
    String json = objectMapper.writeValueAsString(testIri);
    IRI deserialized = objectMapper.readValue(json, IRI.class);

    assertEquals(testIri, deserialized);
  }

  /**
   * Tests IRI with fragment.
   */
  @Test
  void testIriWithFragment() throws Exception {
    IRI iriWithFragment = IRI.create("http://example.org/ontology#Class1");
    String json = objectMapper.writeValueAsString(iriWithFragment);
    IRI deserialized = objectMapper.readValue(json, IRI.class);

    assertEquals(iriWithFragment, deserialized);
  }

  /**
   * Tests IRI with query parameters.
   */
  @Test
  void testIriWithQuery() throws Exception {
    IRI iriWithQuery = IRI.create("http://example.org/ontology?version=1.0");
    String json = objectMapper.writeValueAsString(iriWithQuery);
    IRI deserialized = objectMapper.readValue(json, IRI.class);

    assertEquals(iriWithQuery, deserialized);
  }

  /**
   * Tests IRI as field in a wrapper class.
   */
  @Test
  void testIriInObject() throws Exception {
    IriWrapper wrapper = new IriWrapper(testIri);
    String json = objectMapper.writeValueAsString(wrapper);

    assertNotNull(json);
    assertEquals("{\"iri\":\"http://example.org/ontology\"}", json);

    IriWrapper deserialized = objectMapper.readValue(json, IriWrapper.class);
    assertEquals(wrapper.iri(), deserialized.iri());
  }

  /**
   * Helper record for testing IRI as a field.
   */
  record IriWrapper(IRI iri) {
  }
}
