package edu.stanford.protege.robot.command.extract;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import edu.stanford.protege.robot.command.RobotCommand;
import java.util.Arrays;
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
 * @param extractStrategy
 *          the extraction method strategy (SLME, MIREOT, or Subset)
 * @param extractIntermediates
 *          how to handle intermediate classes in the hierarchy
 * @param handlingImports
 *          whether to include or exclude imported ontologies
 * @param flags
 *          optional behavior flags
 *
 * @see <a href="https://robot.obolibrary.org/extract">ROBOT Extract Documentation</a>
 */
@JsonTypeName("ExtractCommand")
public record RobotExtractCommand(
    ExtractStrategy extractStrategy,
    @Nullable ExtractIntermediates extractIntermediates,
    @Nullable HandlingImports handlingImports,
    ExtractFlags... flags)
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
    // Process flags using Arrays.asList() for varargs
    List<ExtractFlags> flagsList = Arrays.asList(flags);
    if (flagsList.contains(ExtractFlags.COPY_ONTOLOGY_ANNOTATIONS)) {
      args.add(ExtractFlags.COPY_ONTOLOGY_ANNOTATIONS.getFlagName());
      args.add("true");
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
