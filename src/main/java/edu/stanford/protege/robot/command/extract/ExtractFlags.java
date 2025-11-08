package edu.stanford.protege.robot.command.extract;

/**
 * Boolean flags for ROBOT extract command.
 */
public enum ExtractFlags {

  /**
   * This flag enables copying ontology-level annotations to the extracted module.
   */
  COPY_ONTOLOGY_ANNOTATIONS("--copy-ontology-annotations"),
  ;

  private final String flagName;

  ExtractFlags(String flagName) {
    this.flagName = flagName;
  }

  /**
   * Returns the ROBOT CLI flag.
   *
   * @return the flag string.
   */
  public String getFlagName() {
    return flagName;
  }
}
