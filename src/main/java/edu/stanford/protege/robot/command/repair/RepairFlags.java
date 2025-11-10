package edu.stanford.protege.robot.command.repair;

/**
 * Boolean flags for ROBOT repair command.
 *
 * <p>
 * These flags enable specific repair operations for fixing ontology problems.
 */
public enum RepairFlags {

  /**
   * Enable fixing deprecated class references.
   *
   * <p>
   * Updates axioms that reference deprecated classes with their designated replacements (using
   * "term replaced by" property). This addresses issues from Protégé editing or import module
   * rebuilds.
   */
  INVALID_REFERENCES("--invalid-references"),

  /**
   * Enable consolidation of axiom annotations.
   *
   * <p>
   * Consolidates duplicate statements with different axiom annotations into single assertions with
   * combined annotations.
   */
  MERGE_AXIOM_ANNOTATIONS("--merge-axiom-annotations"),
  ;

  private final String flagName;

  RepairFlags(String flagName) {
    this.flagName = flagName;
  }

  /**
   * Returns the ROBOT CLI flag.
   *
   * @return the flag string (e.g., {@code "--invalid-references"})
   */
  public String getFlagName() {
    return flagName;
  }
}
