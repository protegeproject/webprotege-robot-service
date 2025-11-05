package edu.stanford.protege.robot.command.extract;

/**
 * Defines how intermediate classes in the class hierarchy are handled during extraction.
 *
 * <p>
 * Intermediates are classes that appear between seed terms in the ontology hierarchy but were
 * not explicitly requested for extraction. This option controls which of these intermediate
 * classes are retained in the extracted module.
 *
 * @see <a href="https://robot.obolibrary.org/extract#intermediates">ROBOT Intermediates
 *      Documentation</a>
 */
public enum ExtractIntermediates {

  /**
   * Retain the complete hierarchy including all intermediate classes between seed terms.
   *
   * <p>
   * This is the default behavior and ensures that the full path between all seed terms is
   * preserved, maintaining complete hierarchical context.
   */
  all,

  /**
   * Keep only intermediate classes that have multiple children (siblings) in the extracted module.
   *
   * <p>
   * This option preserves important branching points in the hierarchy while removing
   * linear chains of intermediate classes that add little structural information.
   */
  minimal,

  /**
   * Exclude all intermediate classes that were not explicitly requested as seed terms.
   *
   * <p>
   * This option produces the sparsest extraction, containing only the seed terms and their
   * direct relationships, without preserving the intermediate hierarchy.
   */
  none;
}
