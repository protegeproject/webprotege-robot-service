package edu.stanford.protege.robot.command.extract;

import java.util.List;

/**
 * Strategy interface for different ontology extraction methods in ROBOT.
 *
 * <p>
 * Extraction enables reuse of ontology term subsets rather than entire ontologies,
 * creating focused modules containing selected terms and their supporting relationships.
 *
 * <p>
 * Implementations include:
 * <ul>
 * <li>{@link SlmeExtractStrategy} - Syntactic Locality Module Extractor (SLME) that preserves
 * logical entailments</li>
 * <li>{@link MireotExtractStrategy} - MIREOT method that preserves hierarchy structure</li>
 * <li>{@link SubsetExtractStrategy} - Subset method using relation-graph to materialize
 * existential relations</li>
 * </ul>
 *
 * @see <a href="https://robot.obolibrary.org/extract">ROBOT Extract Documentation</a>
 */
public interface ExtractStrategy {

  /**
   * Converts this extraction strategy to command-line arguments for ROBOT.
   *
   * @return a list of command-line arguments representing this extraction strategy
   */
  List<String> getArgs();
}
