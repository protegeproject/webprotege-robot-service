package edu.stanford.protege.robot.extract;

import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * Subset extraction strategy using relation-graph to materialize existential relations.
 *
 * <p>
 * The subset method uses a three-step process:
 * <ol>
 * <li>Materializes the ontology (computing all inferred relationships)</li>
 * <li>Filters to include only seed terms</li>
 * <li>Reduces redundant axioms</li>
 * </ol>
 *
 * <p>
 * This method is useful for extracting terms with their materialized relationships
 * preserved, making implicit relationships explicit in the extracted module.
 *
 * <p>
 * Example usage:
 * 
 * <pre>{@code
 * var strategy = new SubsetExtractStrategy(
 *     List.of("GO:0008150", "GO:0003674", "GO:0005575"));
 * }</pre>
 *
 * @param terms
 *          seed terms to extract (as CURIEs or full IRIs)
 *
 * @see <a href="https://robot.obolibrary.org/extract#subset">ROBOT Subset Documentation</a>
 */
public record SubsetExtractStrategy(List<String> terms) implements ExtractStrategy {

  /**
   * Converts this subset strategy to ROBOT command-line arguments.
   *
   * <p>
   * Generates arguments in the format:
   * {@code --method subset --term TERM1 --term TERM2 ...}
   *
   * @return immutable list of command-line arguments for ROBOT extract
   */
  @Override
  public List<String> getArgs() {
    var args = ImmutableList.<String>builder();
    args.add("--method");
    args.add("subset");
    terms.forEach(
        term -> {
          args.add("--term");
          args.add(term);
        });
    return args.build();
  }
}
