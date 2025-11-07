package edu.stanford.protege.robot.service.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import edu.stanford.protege.webprotege.jackson.IRIMixin;
import org.semanticweb.owlapi.model.IRI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot configuration for Jackson JSON serialization.
 */
@Configuration
public class JacksonConfiguration {

  /**
   * Creates a Jackson module with mix-in annotations for OWL API types.
   *
   * @return Jackson module configured with OWL API mix-ins
   */
  @Bean
  public Module robotJacksonModule() {
    SimpleModule module = new SimpleModule("RobotCommandModule");
    module.setMixInAnnotation(IRI.class, IRIMixin.class);
    return module;
  }
}
