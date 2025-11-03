package edu.stanford.protege.robot.annotate;

/**
 * Boolean flags for ROBOT annotate command.
 *
 * <p>
 * These flags enable special annotation behaviors like interpolation and provenance tracking.
 */
public enum AnnotateFlags {

  INTERPOLATE("--interpolate"),

  ANNOTATE_DERIVED_FROM("--annotate-derived-from"),

  ANNOTATE_DEFINED_BY("--annotate-defined-by"),

  REMOVE_ANNOTATIONS("--remove-annotations"),
  ;

  private final String flagName;

  AnnotateFlags(String flagName) {
    this.flagName = flagName;
  }

  /**
   * Returns the ROBOT CLI flag.
   *
   * @return the flag string (e.g., {@code "--interpolate"})
   */
  public String getFlagName() {
    return flagName;
  }
}
