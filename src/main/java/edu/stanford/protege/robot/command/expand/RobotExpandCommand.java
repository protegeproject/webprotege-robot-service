package edu.stanford.protege.robot.command.expand;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import edu.stanford.protege.robot.command.RobotCommand;
import java.util.List;
import javax.annotation.Nullable;
import org.obolibrary.robot.Command;
import org.obolibrary.robot.ExpandCommand;

/**
 * ROBOT expand command for converting macro annotations into OWL axioms.
 *
 * <p>
 * The expand command automatically converts shortcut annotation properties (macros) into more
 * complex OWL axioms by executing SPARQL CONSTRUCT queries. This is particularly useful for
 * processing macros that generate logical patterns which would be cumbersome to maintain manually,
 * such as part disjointness axioms or complex class expressions.
 *
 * <p>
 * By default, expand processes all SPARQL CONSTRUCT queries associated with "defined by construct"
 * properties in the ontology. You can selectively include or exclude specific macro terms using
 * the expandTerms and noExpandTerms parameters.
 *
 * @param expandTerms
 *          list of term CURIEs or full IRIs to include in expansion (empty list means expand all)
 * @param noExpandTerms
 *          list of term CURIEs or full IRIs to exclude from expansion (empty list means exclude
 *          none)
 * @param annotateExpansionAxioms
 *          if true, adds dct:source annotations linking generated axioms to their source properties
 *          (optional, defaults to false in ROBOT if null)
 * @see <a href="https://robot.obolibrary.org/expand">ROBOT Expand Documentation</a>
 */
@JsonTypeName("ExpandCommand")
public record RobotExpandCommand(
    List<String> expandTerms,
    List<String> noExpandTerms,
    @Nullable Boolean annotateExpansionAxioms)
    implements
      RobotCommand {

  /**
   * Converts this expand command to ROBOT command-line arguments.
   *
   * <p>
   * Generates arguments in the format: {@code [--annotate-expansion-axioms BOOL]
   * [--expand-term TERM]... [--no-expand-term TERM]...}
   *
   * @return immutable list of command-line arguments for ROBOT expand
   */
  @Override
  public List<String> getArgs() {
    var args = ImmutableList.<String>builder();

    // Add annotate-expansion-axioms flag if specified
    if (annotateExpansionAxioms != null) {
      args.add("--annotate-expansion-axioms");
      args.add(String.valueOf(annotateExpansionAxioms));
    }

    // Add expand-term flags (repeated for each term)
    expandTerms.forEach(
        term -> {
          args.add("--expand-term");
          args.add(term);
        });

    // Add no-expand-term flags (repeated for each term)
    noExpandTerms.forEach(
        term -> {
          args.add("--no-expand-term");
          args.add(term);
        });

    return args.build();
  }

  /**
   * Returns the ROBOT ExpandCommand instance for execution.
   *
   * @return a new ExpandCommand instance
   */
  @Override
  public Command getCommand() {
    return new ExpandCommand();
  }
}
