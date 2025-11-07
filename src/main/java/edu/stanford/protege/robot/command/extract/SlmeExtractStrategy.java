package edu.stanford.protege.robot.command.extract;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * SLME (Syntactic Locality Module Extractor) extraction strategy.
 *
 * <p>
 * SLME preserves logical entailments between seed terms and their signatures, making it
 * suitable for creating logically coherent ontology modules. Three extraction methods are
 * available:
 *
 * <ul>
 * <li><b>BOT (Bottom Module)</b> - Recommended default. Includes seed terms, all superclasses,
 * and inter-relations. Takes a view from the bottom of the class hierarchy upwards,
 * producing medium-sized modules.</li>
 * <li><b>TOP (Top Module)</b> - Includes seed terms, all subclasses, and inter-relations.
 * Typically produces large modules. Use only when all descendants are necessary.</li>
 * <li><b>STAR (Star Module)</b> - Produces minimal modules with seed terms and inter-relations
 * only, excluding unnecessary class hierarchies. Smallest extraction type.</li>
 * </ul>
 *
 * <p>
 * Example usage:
 * 
 * <pre>{@code
 * var strategy = new SlmeExtractStrategy(
 *     SlmeExtractMethod.bot,
 *     List.of("GO:0008150", "GO:0003674", "GO:0005575"));
 * }</pre>
 *
 * @param method
 *          the SLME extraction method (bot, top, or star)
 * @param terms
 *          seed terms to extract (as CURIEs or full IRIs)
 * @see <a href="https://robot.obolibrary.org/extract#slme">ROBOT SLME Documentation</a>
 */
@JsonTypeName("SLME")
public record SlmeExtractStrategy(SlmeExtractMethod method, List<String> terms) implements ExtractStrategy {

  /**
   * Converts this SLME strategy to ROBOT command-line arguments.
   *
   * <p>
   * Generates arguments in the format:
   * {@code --method [bot|top|star] --term TERM1 --term TERM2 ...}
   *
   * @return immutable list of command-line arguments for ROBOT extract
   */
  @Override
  public List<String> getArgs() {
    var args = ImmutableList.<String>builder();
    args.add("--method");
    args.add(method.name());
    terms.forEach(
        term -> {
          args.add("--term");
          args.add(term);
        });
    return args.build();
  }
}
