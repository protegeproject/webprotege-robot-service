package edu.stanford.protege.robot.command.convert;

import java.util.List;

/**
 * Base interface for ROBOT convert format strategies.
 *
 * <p>
 * Convert strategies encapsulate format-specific parameters and options for ontology format
 * conversion. Each implementation represents a target format and its configuration options.
 *
 * <p>
 * Implementations include:
 *
 * <ul>
 * <li>{@link OboConvertStrategy} - OBO format with validation and cleaning options</li>
 * <li>{@link JsonConvertStrategy} - OBO Graphs JSON format</li>
 * <li>{@link OfnConvertStrategy} - OWL Functional Syntax</li>
 * <li>{@link OmnConvertStrategy} - Manchester Syntax</li>
 * <li>{@link OwlConvertStrategy} - RDF/XML format</li>
 * <li>{@link OwxConvertStrategy} - OWL/XML format</li>
 * <li>{@link TtlConvertStrategy} - Turtle format</li>
 * </ul>
 *
 * @see <a href="https://robot.obolibrary.org/convert">ROBOT Convert Documentation</a>
 */
public interface ConvertStrategy {

  /**
   * Converts this convert convertStrategy to command-line arguments for ROBOT.
   *
   * @return immutable list of CLI arguments for this conversion format
   */
  List<String> getArgs();
}
