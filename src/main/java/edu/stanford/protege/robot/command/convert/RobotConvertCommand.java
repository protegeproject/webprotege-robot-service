package edu.stanford.protege.robot.command.convert;

import com.google.common.collect.ImmutableList;
import edu.stanford.protege.RobotCommand;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.obolibrary.robot.Command;
import org.obolibrary.robot.ConvertCommand;
import org.semanticweb.owlapi.model.IRI;

/**
 * ROBOT convert command for transforming ontologies between different formats.
 *
 * <p>
 * The convert command enables format transformation between multiple OWL, RDF, and OBO formats.
 * ROBOT automatically detects the target format from the output file extension, but you can
 * explicitly specify it using the format parameter.
 *
 * <p>
 * Supported formats include: JSON (OBO Graphs), OBO (legacy text), OWL Functional Syntax,
 * Manchester Syntax, RDF/XML, OWL/XML, and Turtle. All formats support gzip compression via
 * {@code .gz} file extension.
 *
 * <p>
 * For OBO format conversions, additional options control structure validation and output cleaning:
 *
 * <ul>
 * <li><b>check</b> - Enforces OBO document structure rules (default: true)</li>
 * <li><b>cleanOboOptions</b> - Fine-tunes OBO output by dropping/merging annotations</li>
 * <li><b>addPrefixes</b> - Injects custom namespace prefixes (requires cleanOboOptions)</li>
 * </ul>
 *
 * @param format
 *          target format (optional; auto-detected from output file extension if null)
 * @param check
 *          enforce OBO document structure rules (optional; defaults to true in ROBOT if null)
 * @param cleanOboOptions
 *          fine-tuning keywords for OBO format output (optional; only applies to OBO format)
 * @param addPrefixes
 *          custom namespace prefixes as map of prefix name to IRI (optional; requires
 *          cleanOboOptions)
 * @see <a href="https://robot.obolibrary.org/convert">ROBOT Convert Documentation</a>
 */
public record RobotConvertCommand(
    @Nullable ConvertFormat format,
    @Nullable Boolean check,
    @Nullable List<CleanOboOption> cleanOboOptions,
    @Nullable Map<String, IRI> addPrefixes)
    implements
      RobotCommand {

  /**
   * Converts this convert command to ROBOT command-line arguments.
   *
   * <p>
   * Generates arguments in the format: {@code [--format FORMAT] [--check BOOL] [--clean-obo
   * KEYWORD ...] [--add-prefix "PREFIX: IRI"]...}
   *
   * @return immutable list of command-line arguments for ROBOT convert
   */
  @Override
  public List<String> getArgs() {
    var args = ImmutableList.<String>builder();

    // Add format if explicitly specified
    if (format != null) {
      args.add("--format");
      args.add(format.name());
    }

    // Add check flag if specified
    if (check != null) {
      args.add("--check");
      args.add(String.valueOf(check));
    }

    // Add clean-obo options as space-separated keywords
    if (cleanOboOptions != null && !cleanOboOptions.isEmpty()) {
      args.add("--clean-obo");
      // Join all keywords with spaces
      var cleanOboKeywords = cleanOboOptions.stream()
          .map(CleanOboOption::getKeyword)
          .reduce((a, b) -> a + " " + b)
          .orElse("");
      args.add(cleanOboKeywords);
    }

    // Add custom prefixes (only works with --clean-obo)
    if (addPrefixes != null && !addPrefixes.isEmpty()) {
      addPrefixes.forEach(
          (prefix, iri) -> {
            args.add("--add-prefix");
            args.add(prefix + ": " + iri);
          });
    }

    return args.build();
  }

  /**
   * Returns the ROBOT ConvertCommand instance for execution.
   *
   * @return a new ConvertCommand instance
   */
  @Override
  public Command getCommand() {
    return new ConvertCommand();
  }
}
