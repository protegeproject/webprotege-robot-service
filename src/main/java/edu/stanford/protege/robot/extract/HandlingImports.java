package edu.stanford.protege.robot.extract;

/**
 * Defines how imported ontologies are handled during extraction.
 *
 * <p>
 * Many ontologies import terms from other ontologies. This option controls whether those
 * imported ontologies are included in the extraction process or if only the asserted content
 * of the main ontology is processed.
 *
 * @see <a href="https://robot.obolibrary.org/extract">ROBOT Extract Documentation</a>
 */
public enum HandlingImports {

  /**
   * Include imported ontologies in the extraction process.
   *
   * <p>
   * This is the default behavior and ensures that terms from imported ontologies are
   * available during extraction, which may be necessary if seed terms reference imported classes.
   */
  include,

  /**
   * Exclude imported ontologies and process only the asserted content of the main ontology.
   *
   * <p>
   * This option restricts extraction to only the terms and axioms explicitly asserted in the
   * main ontology, ignoring any imported content.
   */
  exclude,
}
