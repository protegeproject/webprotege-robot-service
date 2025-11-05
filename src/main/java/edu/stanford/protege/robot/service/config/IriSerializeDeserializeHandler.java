package edu.stanford.protege.robot.service.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.semanticweb.owlapi.model.IRI;
import org.springframework.boot.jackson.JsonComponent;

/**
 * Spring Boot Jackson component for IRI serialization and deserialization.
 *
 * <p>
 * This component provides custom JSON handling for OWL API {@link IRI} objects, enabling
 * seamless conversion between IRI instances and JSON string representations. Spring Boot
 * automatically registers this component with the application's ObjectMapper.
 *
 * <p>
 * Serialization converts IRI objects to their string representation (e.g., {@code
 * "http://example.org/ontology"}). Deserialization parses JSON strings into IRI instances using
 * {@link IRI#create(String)}.
 */
@JsonComponent
public class IriSerializeDeserializeHandler {

  /**
   * Jackson serializer that converts IRI objects to JSON strings.
   *
   * <p>
   * Serializes an IRI by writing its string representation to the JSON output. This enables IRI
   * fields in Java objects to be automatically serialized to readable JSON strings.
   */
  public static class Serializer extends JsonSerializer<IRI> {
    @Override
    public void serialize(IRI value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      gen.writeString(value.toString());
    }
  }

  /**
   * Jackson deserializer that converts JSON strings to IRI objects.
   *
   * <p>
   * Deserializes a JSON string by parsing it with {@link IRI#create(String)}. This enables JSON
   * string values to be automatically converted to IRI instances when deserializing Java objects.
   *
   * @throws IOException
   *           if the JSON value cannot be parsed as a valid IRI string
   */
  public static class Deserializer extends JsonDeserializer<IRI> {
    @Override
    public IRI deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return IRI.create(p.getValueAsString());
    }
  }
}
