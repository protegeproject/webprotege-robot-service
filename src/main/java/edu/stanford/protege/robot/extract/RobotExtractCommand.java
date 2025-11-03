package edu.stanford.protege.robot.extract;

import com.google.common.collect.ImmutableList;
import edu.stanford.protege.RobotCommand;
import java.util.List;
import javax.annotation.Nullable;
import org.obolibrary.robot.Command;
import org.obolibrary.robot.ExtractCommand;

/**
 * Main command for extracting ontology term subsets using ROBOT's extract functionality.
 *
 * <p>
 * The extract command enables reuse of ontology term subsets rather than entire ontologies,
 * creating focused modules containing selected terms and their supporting relationships while
 * preserving logical entailments or hierarchy structure depending on the chosen strategy.
 *
 * <p>
 * Example usage with SLME BOT method:
 * 
 * <pre>{@code
 * var strategy = new SlmeExtractStrategy(
 *     SlmeExtractMethod.BOT,
 *     List.of("GO:0008150", "GO:0003674"));
 * var command = new RobotExtractCommand(
 *     strategy,
 *     ExtractIntermediates.minimal,
 *     HandlingImports.include,
 *     true // copy ontology annotations
 * );
 * // Generates: --method BOT --term GO:0008150 --term GO:0003674
 * // --intermediates minimal --imports include
 * // --copy-ontology-annotations true
 * }</pre>
 *
 * <p>
 * Example usage with MIREOT method:
 * 
 * <pre>{@code
 * var strategy = new MireotExtractStrategy(
 *     List.of("GO:0008150"), // upper
 *     List.of("GO:0009987"), // lower
 *     List.of() // branch-from
 * );
 * var command = new RobotExtractCommand(strategy, null, null, true);
 * }</pre>
 *
 * @param extractStrategy
 *          the extraction method strategy (SLME, MIREOT, or Subset)
 * @param extractIntermediates
 *          how to handle intermediate classes in the hierarchy
 * @param handlingImports
 *          whether to include or exclude imported ontologies
 * @param copyOntologyAnnotations
 *          whether to preserve ontology-level annotations in the extract
 *
 * @see <a href="https://robot.obolibrary.org/extract">ROBOT Extract Documentation</a>
 */
public record RobotExtractCommand(
    ExtractStrategy extractStrategy,
    @Nullable ExtractIntermediates extractIntermediates,
    @Nullable HandlingImports handlingImports,
    @Nullable Boolean copyOntologyAnnotations)
    implements
      RobotCommand {

  /**
   * Converts this extract command to ROBOT command-line arguments.
   *
   * <p>
   * Generates arguments for intermediate handling, imports, and annotation copying options.
   * The extraction strategy arguments are included via {@link ExtractStrategy#getArgs()}.
   *
   * @return immutable list of command-line arguments for ROBOT extract
   */
  @Override
  public List<String> getArgs() {
    var args = ImmutableList.<String>builder();
    args.addAll(extractStrategy.getArgs());
    if (extractIntermediates != null) {
      args.add("--intermediates");
      args.add(extractIntermediates.name());
    }
    if (handlingImports != null) {
      args.add("--imports");
      args.add(handlingImports.name());
    }
    if (copyOntologyAnnotations != null) {
      args.add("--copy-ontology-annotations");
      args.add(String.valueOf(copyOntologyAnnotations));
    }
    return args.build();
  }

  /**
   * Returns the ROBOT ExtractCommand instance for execution.
   *
   * @return a new ExtractCommand instance
   */
  @Override
  public Command getCommand() {
    return new ExtractCommand();
  }
}
