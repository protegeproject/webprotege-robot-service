package edu.stanford.protege.robot;

import org.obolibrary.robot.CommandState;
import org.obolibrary.robot.IOHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Spring configuration for ROBOT command execution components.
 */
@Configuration
public class RobotServiceConfiguration {

  /**
   * Provides a prototype-scoped CommandState bean.
   *
   * <p>
   * Each call to get this bean (either via direct injection or through a Provider) will return a
   * new instance, ensuring clean state for command chain executions.
   *
   * @return a new CommandState instance
   */
  @Bean
  @Scope("prototype")
  public CommandState commandState() {
    return new CommandState();
  }

  /**
   * Provides a singleton IOHelper bean for ontology I/O operations.
   *
   * <p>
   * The IOHelper is stateless and can be safely shared across multiple concurrent executions.
   *
   * @return a new IOHelper instance
   * @throws RuntimeException
   *           if IOHelper initialization fails
   */
  @Bean
  public IOHelper ioHelper() {
    try {
      return new IOHelper();
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize IOHelper", e);
    }
  }
}
