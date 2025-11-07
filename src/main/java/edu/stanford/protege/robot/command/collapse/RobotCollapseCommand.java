package edu.stanford.protege.robot.command.collapse;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import edu.stanford.protege.robot.command.RobotCommand;
import java.util.List;
import javax.annotation.Nullable;
import org.obolibrary.robot.CollapseCommand;
import org.obolibrary.robot.Command;

/**
 * ROBOT collapse command for streamlining class hierarchies.
 *
 * <p>
 * The collapse command removes unnecessary intermediate classes from ontology hierarchies based on
 * subclass count thresholds. This is particularly useful after extracting modules from large
 * ontologies, where the resulting hierarchy may contain intermediate classes that don't add
 * meaningful structure.
 *
 * <p>
 * Intermediate classes are classes that have both superclasses and subclasses. The collapse command
 * evaluates each intermediate class and removes those with fewer subclasses than the specified
 * threshold, unless they are marked as "precious" (protected from removal).
 *
 * <p>
 * Classes that are always preserved:
 *
 * <ul>
 * <li><b>Root classes</b> - Classes without superclasses (top-level in the hierarchy)</li>
 * <li><b>Leaf classes</b> - Classes without subclasses (bottom-level in the hierarchy)</li>
 * <li><b>Precious classes</b> - Classes explicitly marked for preservation via the precious terms
 * list</li>
 * </ul>
 *
 * @param threshold
 *          minimum subclass count to preserve intermediate classes (optional, defaults to 2 if
 *          null). Must be greater than 2 if specified.
 * @param preciousTerms
 *          list of term CURIEs or full IRIs to protect from removal (empty list means no protected
 *          terms)
 *
 * @see <a href="https://robot.obolibrary.org/collapse">ROBOT Collapse Documentation</a>
 */
@JsonTypeName("CollapseCommand")
public record RobotCollapseCommand(@Nullable Integer threshold, List<String> preciousTerms)
    implements
      RobotCommand {

  /**
   * Converts this collapse command to ROBOT command-line arguments.
   *
   * <p>
   * Generates arguments in the format: {@code [--threshold N] [--precious TERM]...}
   *
   * <p>
   * The threshold parameter is optional and will be omitted if null (allowing ROBOT to use its
   * default value of 2). Precious terms are added as repeated {@code --precious} flags, one for
   * each term in the list.
   *
   * @return immutable list of command-line arguments for ROBOT collapse
   */
  @Override
  public List<String> getArgs() {
    var args = ImmutableList.<String>builder();

    // Add threshold if specified
    if (threshold != null) {
      args.add("--threshold");
      args.add(String.valueOf(threshold));
    }

    // Add precious terms (repeated --precious flag)
    preciousTerms.forEach(
        term -> {
          args.add("--precious");
          args.add(term);
        });

    return args.build();
  }

  /**
   * Returns the ROBOT CollapseCommand instance for execution.
   *
   * @return a new CollapseCommand instance
   */
  @Override
  public Command getCommand() {
    return new CollapseCommand();
  }
}
