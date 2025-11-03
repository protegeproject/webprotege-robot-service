package edu.stanford.protege.robot.annotate;

/**
 * Marker interface for annotations with language tags.
 *
 * <p>
 * Indicates that an annotation includes a language code for internationalized text.
 */
public interface HasLang {

  /**
   * Returns the ISO 639-1 language code.
   *
   * @return language code (e.g., "en", "fr", "de")
   */
  String lang();
}
