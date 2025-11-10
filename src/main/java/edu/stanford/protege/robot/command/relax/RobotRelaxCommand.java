package edu.stanford.protege.robot.command.relax;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import edu.stanford.protege.robot.command.RobotCommand;
import java.util.Arrays;
import java.util.List;
import org.obolibrary.robot.Command;
import org.obolibrary.robot.RelaxCommand;

/**
 * ROBOT relax command for converting equivalence axioms into SubClassOf axioms.
 *
 * <p>
 * The relax command converts OWL EquivalenceAxioms into weaker SubClassOf axioms, making ontologies
 * more consumable by applications that only process SubClassOf relationships. This is particularly
 * useful for graph-based applications and visualization tools.
 *
 * <p>
 * Common workflow: {@code reason → relax → reduce} produces a complete, non-redundant SubClassOf
 * graph suitable for visualization and browsing.
 *
 * @param flags
 *          boolean flags for non-default relaxation behaviors. Available flags: {@link
 *          RelaxFlags#INCLUDE_NAMED_CLASSES}, {@link RelaxFlags#INCLUDE_SUBCLASS_OF}, {@link
 *          RelaxFlags#ENFORCE_OBO_FORMAT}. If not specified, ROBOT defaults are used (exclude
 *          named class equivalences, do not relax SubClassOf axioms, no OBO format enforcement).
 * @see <a href="https://robot.obolibrary.org/relax">ROBOT Relax Documentation</a>
 */
@JsonTypeName("RelaxCommand")
public record RobotRelaxCommand(RelaxFlags... flags) implements RobotCommand {

  /**
   * Converts this relax command to ROBOT command-line arguments.
   *
   * <p>
   * Generates arguments for controlling how equivalence axioms and complex expressions are relaxed.
   * If no flags are specified, an empty list is returned and ROBOT's default behavior applies
   * (exclude named class equivalences, do not relax SubClassOf axioms).
   *
   * @return immutable list of command-line arguments for ROBOT relax
   */
  @Override
  public List<String> getArgs() {
    var args = ImmutableList.<String>builder();

    // Add flags (each flag sets a non-default value)
    List<RelaxFlags> flagsList = Arrays.asList(flags);
    if (flagsList.contains(RelaxFlags.INCLUDE_NAMED_CLASSES)) {
      args.add(RelaxFlags.INCLUDE_NAMED_CLASSES.getFlagName());
      args.add("false");
    }
    if (flagsList.contains(RelaxFlags.INCLUDE_SUBCLASS_OF)) {
      args.add(RelaxFlags.INCLUDE_SUBCLASS_OF.getFlagName());
      args.add("true");
    }
    if (flagsList.contains(RelaxFlags.ENFORCE_OBO_FORMAT)) {
      args.add(RelaxFlags.ENFORCE_OBO_FORMAT.getFlagName());
      args.add("true");
    }

    return args.build();
  }

  /**
   * Returns the ROBOT RelaxCommand instance for execution.
   *
   * @return a new RelaxCommand instance
   */
  @Override
  public Command getCommand() {
    return new RelaxCommand();
  }
}
