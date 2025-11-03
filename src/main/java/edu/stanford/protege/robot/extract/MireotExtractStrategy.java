package edu.stanford.protege.robot.extract;

import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * MIREOT (Minimum Information to Reference External Ontology Term) extraction strategy.
 *
 * <p>
 * MIREOT preserves hierarchy structure (subclass/subproperty relationships) without ensuring
 * full logical entailment preservation. It is useful for extracting term hierarchies with
 * upper and lower boundaries.
 *
 * <p>
 * Example usage:
 * 
 * <pre>{@code
 * var strategy = new MireotExtractStrategy(
 *     List.of("GO:0008150"), // upper term (root)
 *     List.of("GO:0009987"), // lower term (boundary - descendants excluded)
 *     List.of("GO:0008152") // branch from term (all descendants included)
 * );
 * }</pre>
 *
 * @param upperTerms
 *          optional upper boundary terms (defaults to root if empty)
 * @param lowerTerms
 *          required lower boundary terms (descendants will be excluded)
 * @param branchFromTerms
 *          terms from which to extract all descendants
 *
 * @see <a href="https://robot.obolibrary.org/extract#mireot">ROBOT MIREOT Documentation</a>
 */
public record MireotExtractStrategy(
    List<String> upperTerms, List<String> lowerTerms, List<String> branchFromTerms) implements ExtractStrategy {

  /**
   * Converts this MIREOT strategy to ROBOT command-line arguments.
   *
   * <p>
   * Generates arguments in the format:
   * {@code --method MIREOT [--upper-term TERM]... [--lower-term TERM]... [--branch-from-term TERM]...}
   *
   * @return immutable list of command-line arguments for ROBOT extract
   */
  @Override
  public List<String> getArgs() {
    var args = ImmutableList.<String>builder();
    args.add("--method");
    args.add("MIREOT");
    upperTerms.forEach(
        term -> {
          args.add("--upper-term");
          args.add(term);
        });
    lowerTerms.forEach(
        term -> {
          args.add("--lower-term");
          args.add(term);
        });
    branchFromTerms.forEach(
        term -> {
          args.add("--branch-from-term");
          args.add(term);
        });
    return args.build();
  }
}
