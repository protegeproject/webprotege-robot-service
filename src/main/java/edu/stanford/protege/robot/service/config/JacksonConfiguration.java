package edu.stanford.protege.robot.service.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import edu.stanford.protege.RobotCommand;
import edu.stanford.protege.robot.command.annotate.Annotation;
import edu.stanford.protege.robot.command.extract.ExtractStrategy;
import org.semanticweb.owlapi.model.IRI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot configuration for customizing Jackson JSON serialization/deserialization.
 *
 * <p>
 * This configuration provides a custom Jackson {@link Module} that registers mix-in annotations
 * for polymorphic type handling. Spring Boot automatically detects and registers any Module beans
 * with the application's ObjectMapper, enabling transparent JSON conversion for ROBOT command
 * objects.
 *
 * <p>
 * The configuration handles three key polymorphic type hierarchies:
 *
 * <ul>
 * <li>{@link RobotCommand} - 5 command implementations (annotate, extract, collapse, convert,
 * expand)</li>
 * <li>{@link Annotation} - 4 annotation types (plain, typed, language, link)</li>
 * <li>{@link ExtractStrategy} - 3 extraction strategies (slme, mireot, subset)</li>
 * </ul>
 *
 * <p>
 * Mix-ins use Jackson's {@code @JsonTypeInfo} and {@code @JsonSubTypes} annotations to enable
 * type discrimination during deserialization. The {@code type} field in JSON determines which
 * concrete class to instantiate.
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
   * Creates a Jackson module with mix-in annotations for ROBOT command polymorphism.
   *
   * <p>
   * Spring Boot automatically registers this module with the application's ObjectMapper. Mix-ins
   * provide type discrimination without modifying the original domain model classes, maintaining
   * separation of concerns between domain logic and JSON serialization.
   *
   * @return Jackson module configured with ROBOT command mix-ins
   */
  @Bean
  public Module robotJacksonModule() {
    SimpleModule module = new SimpleModule("RobotCommandModule");

    // Register mix-ins for polymorphic type handling
    module.setMixInAnnotation(RobotCommand.class, RobotCommandMixin.class);
    module.setMixInAnnotation(Annotation.class, AnnotationMixin.class);
    module.setMixInAnnotation(ExtractStrategy.class, ExtractStrategyMixin.class);

    // Register custom serializers/deserializers for IRI
    module.addSerializer(IRI.class, new IriSerializeDeserializeHandler.Serializer());
    module.addDeserializer(IRI.class, new IriSerializeDeserializeHandler.Deserializer());

    return module;
  }
}
