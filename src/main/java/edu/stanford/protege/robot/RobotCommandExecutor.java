package edu.stanford.protege.robot;

import edu.stanford.protege.RobotCommand;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Provider;
import org.obolibrary.robot.CommandState;
import org.obolibrary.robot.IOHelper;
import org.springframework.stereotype.Service;

/**
 * Spring service for executing chains of ROBOT commands sequentially, threading state between
 * commands.
 *
 * <p>
 * This executor enables programmatic composition of ROBOT command pipelines, similar to ROBOT's
 * command-line chaining. Each command receives the output from the previous command via
 * CommandState, creating processing pipelines without intermediate file I/O.
 *
 * <p>
 * Example usage with Spring injection:
 *
 * <pre>{@code
 * &#64;Autowired
 * private RobotCommandExecutor executor;
 *
 * public void processOntology() {
 *   // Create command chain: annotate then extract
 *   var annotateCmd = ...
 *   var extractCmd = ...
 *
 *   // Execute chain
 *   var result = executor.executeChain(
 *       "input.owl",
 *       List.of(annotateCmd, extractCmd),
 *       "output.owl");
 * }
 * }</pre>
 *
 * <p>
 * This service is a Spring singleton and is thread-safe for concurrent use.
 *
 * @see RobotCommand
 * @see <a href="https://robot.obolibrary.org/chaining">ROBOT Chaining Documentation</a>
 */
@Service
public class RobotCommandExecutor {

  private final Provider<CommandState> commandStateProvider;
  private final IOHelper ioHelper;

  /**
   * Creates a new RobotCommandExecutor with Spring-injected dependencies.
   *
   * @param commandStateProvider
   *          provider for CommandState instances
   * @param ioHelper
   *          helper for ontology I/O operations
   */
  public RobotCommandExecutor(
      Provider<CommandState> commandStateProvider, IOHelper ioHelper) {
    this.commandStateProvider = commandStateProvider;
    this.ioHelper = ioHelper;
  }

  /**
   * Executes a chain of ROBOT commands sequentially.
   *
   * <p>
   * Commands receive the output from the previous command automatically. The first command receives
   * the loaded input ontology, and subsequent commands receive the modified ontology from their
   * predecessor.
   *
   * @param ontologyFilePath
   *          path to the input ontology file (required)
   * @param commands
   *          list of commands to execute in sequence (required, must not be empty)
   * @param outputPath
   *          path to save the final ontology (optional, nullable)
   * @return the final CommandState after all commands have executed
   * @throws IllegalArgumentException
   *           if ontologyFilePath is null, commands is null or empty
   * @throws Exception
   *           if any command fails or I/O error occurs
   */
  public CommandState executeChain(@Nonnull Path ontologyFilePath,
      @Nonnull List<RobotCommand> commands,
      @Nonnull Path outputPath) throws Exception {

    // Validate inputs
    Objects.requireNonNull(ontologyFilePath, "ontologyFilePath cannot be null");
    Objects.requireNonNull(commands, "commands cannot be null");
    if (commands.isEmpty()) {
      throw new IllegalArgumentException("commands cannot be empty");
    }
    Objects.requireNonNull(outputPath, "outputPath cannot be null");

    // Get fresh CommandState
    var state = commandStateProvider.get();

    // Load input ontology
    var ontology = ioHelper.loadOntology(ontologyFilePath.toFile());
    state.setOntology(ontology);
    state.setOntologyPath(ontologyFilePath.toString());

    // Execute each command sequentially, threading state between them
    for (RobotCommand robotCommand : commands) {
      var command = robotCommand.getCommand();
      var args = robotCommand.getArgsArray();
      state = command.execute(state, args);

      // Verify state is not null after execution
      if (state == null) {
        throw new IllegalStateException(
            "Command " + command.getName() + " returned null state");
      }
    }

    // Save output
    ioHelper.saveOntology(state.getOntology(), outputPath.toString());

    return state;
  }
}
