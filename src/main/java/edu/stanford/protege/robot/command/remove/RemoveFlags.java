package edu.stanford.protege.robot.command.remove;

public enum RemoveFlags {

  /**
   * Match named objects only when removing axioms.
   *
   * <p>
   * Generates {@code --signature true}. When specified, only axioms containing named entities
   * (with IRIs) are considered for removal, excluding anonymous entities.
   */
  SIGNATURE("--signature"),

  /**
   * Remove axiom only if ALL objects in the axiom match the target set.
   *
   * <p>
   * Generates {@code --trim false}. When specified, an axiom is only removed if every object in it
   * matches the target set. Useful when removing imports to preserve dangling references.
   */
  NO_TRIM("--trim"),

  /**
   * Do not maintain hierarchy structure when removing classes.
   *
   * <p>
   * Generates {@code --preserve-structure false}. When specified, hierarchical connections are not
   * preserved when classes are removed, potentially creating disconnected subgraphs.
   */
  NO_PRESERVE_STRUCTURE("--preserve-structure"),

  /**
   * Enable handling of punned entities.
   *
   * <p>
   * Generates {@code --allow-punning true}. Punning occurs when the same IRI is used for different
   * entity types (e.g., as both a class and a property). When specified, punned entities are
   * processed instead of being ignored.
   */
  ALLOW_PUNNING("--allow-punning"),
  ;

  private final String flagName;

  RemoveFlags(String flagName) {
    this.flagName = flagName;
  }

  /**
   * Returns the ROBOT CLI flag name.
   *
   * @return the flag string (e.g., {@code "--signature"})
   */
  public String getFlagName() {
    return flagName;
  }
}
