package edu.stanford.protege.robot.command.convert;

/**
 * Supported ontology formats for ROBOT convert command.
 *
 * <p>
 * ROBOT can convert between multiple OWL, RDF, and OBO formats. All formats support gzip
 * compression via {@code .gz} file extension.
 *
 * @see <a href="https://robot.obolibrary.org/convert">ROBOT Convert Documentation</a>
 */
public enum ConvertFormat {

  // OBO Graphs JSON format.
  json,

  // OBO Format (legacy text format).
  obo,

  // OWL Functional Syntax.
  ofn,

  // Manchester Syntax.
  omn,

  // RDF/XML (default ROBOT format).
  owl,

  // OWL/XML.
  owx,

  // Turtle (Terse RDF Triple Language).
  ttl;
}
