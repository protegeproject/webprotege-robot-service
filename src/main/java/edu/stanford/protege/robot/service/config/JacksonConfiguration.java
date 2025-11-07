package edu.stanford.protege.robot.service.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.semanticweb.owlapi.model.IRI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot configuration for customizing Jackson JSON serialization/deserialization.
 *
 * <p>
 * This configuration provides a custom Jackson {@link Module} that registers custom
 * serializers/deserializers for OWL API types. Spring Boot automatically detects and registers
 * any Module beans with the application's ObjectMapper.
 *
 * <p>
 * Currently handles:
 *
 * <ul>
 * <li>{@link IRI} - Custom serialization/deserialization for OWL API IRIs</li>
 * </ul>
 *
 * <p>
 * Polymorphic type handling for ROBOT commands is achieved through {@code @JsonTypeInfo} and
 * {@code @JsonTypeName} annotations directly on the domain classes, eliminating the need for
 * mix-in classes.
 *
 * <p>
 * Example usage in a Spring service:
 *
 * <pre>{@code
 * @Service
 * public class MyService {
 *   private final ObjectMapper objectMapper;
 *
 *   public MyService(ObjectMapper objectMapper) {
 *     this.objectMapper = objectMapper; // Auto-configured with this module
 *   }
 *
 *   public RobotCommand parseJson(String json) throws JsonProcessingException {
 *     return objectMapper.readValue(json, RobotCommand.class);
 *   }
 * }
 * }</pre>
 */
@Configuration
public class JacksonConfiguration {

  /**
   * Creates a Jackson module with custom serializers/deserializers for OWL API types.
   *
   * <p>
   * Spring Boot automatically registers this module with the application's ObjectMapper.
   *
   * @return Jackson module configured with custom serializers/deserializers
   */
  @Bean
  public Module robotJacksonModule() {
    SimpleModule module = new SimpleModule("RobotCommandModule");

    // Register custom serializers/deserializers for IRI
    module.addSerializer(IRI.class, new IriSerializeDeserializeHandler.Serializer());
    module.addDeserializer(IRI.class, new IriSerializeDeserializeHandler.Deserializer());

    return module;
  }
}
