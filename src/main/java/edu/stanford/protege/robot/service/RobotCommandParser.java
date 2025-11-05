package edu.stanford.protege.robot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.RobotCommand;
import java.util.List;
import javax.annotation.Nonnull;
import org.springframework.stereotype.Component;

/**
 * Spring component responsible for parsing JSON into ROBOT command objects.
 *
 * <p>
 * The parser uses Spring Boot's auto-configured ObjectMapper with custom Jackson configuration
 * for polymorphic type handling and IRI serialization.
 *
 * <p>
 * Example usage:
 *
 * <pre>{@code
 * @Autowired
 * private RobotCommandJsonParser parser;
 *
 * public void parseAndInspect(String json) throws JsonProcessingException {
 *   var output = parser.parseRequest(json);
 *   System.out.println("Input: " + output.inputOntologyPath());
 *   System.out.println("Commands: " + output.commands().size());
 * }
 * }</pre>
 */
@Component
public class RobotCommandParser {

  private final ObjectMapper objectMapper;

  /**
   * Creates a new RobotJsonParser with Spring-injected ObjectMapper.
   *
   * <p>
   * The ObjectMapper is auto-configured by Spring Boot and includes our custom Jackson module for
   * ROBOT-specific type handling.
   *
   * @param objectMapper
   *          Spring Boot auto-configured ObjectMapper with ROBOT customizations
   */
  public RobotCommandParser(@Nonnull ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * Parses a single JSON command object into a RobotCommand.
   *
   * <p>
   * This method deserializes a single polymorphic command object using the {@code @type}
   * discriminator field. The command is instantiated as the appropriate concrete class based on its
   * type.
   *
   * @param commandJson
   *          JSON string representing a single ROBOT command
   * @return deserialized RobotCommand object
   * @throws JsonProcessingException
   *           if JSON is malformed or cannot be deserialized to RobotCommand
   *
   */
  public RobotCommand parseCommand(String commandJson) throws JsonProcessingException {
    return objectMapper.readValue(commandJson, RobotCommand.class);
  }

  /**
   * Parses a JSON array of commands into a list of RobotCommand objects.
   *
   * <p>
   * This method deserializes polymorphic command objects using the {@code @type} discriminator
   * field. Each command is instantiated as the appropriate concrete class based on its type.
   *
   * @param commandsJson
   *          JSON array string containing ROBOT commands
   * @return a list of deserialized RobotCommand objects
   * @throws JsonProcessingException
   *           if JSON is malformed or cannot be deserialized to RobotCommand objects
   */
  public List<RobotCommand> parseCommands(String commandsJson) throws JsonProcessingException {
    var valueType = objectMapper.getTypeFactory().constructCollectionType(List.class, RobotCommand.class);
    return objectMapper.readValue(commandsJson, valueType);
  }
}
