package edu.stanford.protege.robot.command.remove;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import edu.stanford.protege.robot.command.RobotCommand;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import org.obolibrary.robot.Command;
import org.obolibrary.robot.RemoveCommand;

/**
 * ROBOT remove command for eliminating selected axioms from ontologies.
 *
 * <p>
 * The remove command is the inverse of filter, enabling precise axiom deletion through a four-step
 * process: term selection, selector expansion, axiom type specification, and matching/removal.
 *
 * @param baseIri
 *          base namespace for internal/external axiom classification (required for {@code internal}
 *          and {@code external} axiom types)
 * @param terms
 *          list of target terms to remove (CURIEs or IRIs)
 * @param excludeTerms
 *          terms to protect from removal
 * @param includeTerms
 *          terms to force inclusion in removal set
 * @param selectors
 *          filters to expand/narrow target set (use RemoveSelector enum values via {@code .name()}
 *          or custom pattern strings)
 * @param axioms
 *          axiom types to consider (use RemoveAxiomType enum values via {@code .name()} or custom
 *          strings)
 * @param dropAxiomAnnotations
 *          annotation properties to strip from axioms after removal
 * @param flags
 *          boolean flags for non-default removal behaviors. Available flags: {@link
 *          RemoveFlags#SIGNATURE}, {@link RemoveFlags#NO_TRIM}, {@link
 *          RemoveFlags#NO_PRESERVE_STRUCTURE}, {@link RemoveFlags#ALLOW_PUNNING}. If not
 *          specified, ROBOT defaults are used.
 * @see <a href="https://robot.obolibrary.org/remove">ROBOT Remove Documentation</a>
 */
@JsonTypeName("RemoveCommand")
public record RobotRemoveCommand(
    @Nullable String baseIri,
    @Nullable List<String> terms,
    @Nullable List<String> excludeTerms,
    @Nullable List<String> includeTerms,
    @Nullable List<String> selectors,
    @Nullable List<String> axioms,
    @Nullable List<String> dropAxiomAnnotations,
    RemoveFlags... flags)
    implements
      RobotCommand {

  /**
   * Converts this remove command to ROBOT command-line arguments.
   *
   * <p>
   * Generates arguments for term selection, selector expansion, axiom type filtering, and various
   * removal options. All parameters are optional; if none are specified, an empty list is
   * returned.
   *
   * @return immutable list of command-line arguments for ROBOT remove
   */
  @Override
  public List<String> getArgs() {
    var args = ImmutableList.<String>builder();
    // Add base IRI
    if (baseIri != null && !baseIri.isEmpty()) {
      args.add("--base-iri");
      args.add(baseIri);
    }
    // Add terms
    if (terms != null && !terms.isEmpty()) {
      terms.forEach(
          term -> {
            args.add("--term");
            args.add(term);
          });
    }
    // Add exclude terms
    if (excludeTerms != null && !excludeTerms.isEmpty()) {
      excludeTerms.forEach(
          term -> {
            args.add("--exclude-term");
            args.add(term);
          });
    }
    // Add include terms
    if (includeTerms != null && !includeTerms.isEmpty()) {
      includeTerms.forEach(
          term -> {
            args.add("--include-term");
            args.add(term);
          });
    }
    // Add selectors
    if (selectors != null && !selectors.isEmpty()) {
      selectors.forEach(
          selector -> {
            args.add("--select");
            args.add(selector);
          });
    }
    // Add axiom types
    if (axioms != null && !axioms.isEmpty()) {
      axioms.forEach(
          axiom -> {
            args.add("--axioms");
            args.add(axiom);
          });
    }
    // Add drop-axiom-annotations
    if (dropAxiomAnnotations != null && !dropAxiomAnnotations.isEmpty()) {
      dropAxiomAnnotations.forEach(
          property -> {
            args.add("--drop-axiom-annotations");
            args.add(property);
          });
    }

    // Add flags (each flag sets a non-default value)
    List<RemoveFlags> flagsList = Arrays.asList(flags);
    if (flagsList.contains(RemoveFlags.SIGNATURE)) {
      args.add(RemoveFlags.SIGNATURE.getFlagName());
      args.add("true");
    }
    if (flagsList.contains(RemoveFlags.NO_TRIM)) {
      args.add(RemoveFlags.NO_TRIM.getFlagName());
      args.add("false");
    }
    if (flagsList.contains(RemoveFlags.NO_PRESERVE_STRUCTURE)) {
      args.add(RemoveFlags.NO_PRESERVE_STRUCTURE.getFlagName());
      args.add("false");
    }
    if (flagsList.contains(RemoveFlags.ALLOW_PUNNING)) {
      args.add(RemoveFlags.ALLOW_PUNNING.getFlagName());
      args.add("true");
    }

    return args.build();
  }

  /**
   * Returns the ROBOT RemoveCommand instance for execution.
   *
   * @return a new RemoveCommand instance
   */
  @Override
  public Command getCommand() {
    return new RemoveCommand();
  }
}
