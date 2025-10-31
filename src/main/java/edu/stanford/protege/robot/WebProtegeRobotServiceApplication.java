package edu.stanford.protege.robot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the WebProtégé ROBOT Service.
 *
 * <p>
 * This service integrates ROBOT (an OBO ontology tool) with WebProtégé for automated ontology
 * processing and manipulation.
 *
 * @author Josef Hardi
 */
@SpringBootApplication
public class WebProtegeRobotServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(WebProtegeRobotServiceApplication.class, args);
  }
}
