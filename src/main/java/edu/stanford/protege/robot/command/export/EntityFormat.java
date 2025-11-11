package edu.stanford.protege.robot.command.export;

/**
 * Entity rendering format options for ROBOT export command.
 *
 * <p>
 * Controls how entities are displayed in the exported output, affecting both global rendering and
 * column-specific formatting.
 */
public enum EntityFormat {

  /**
   * Render entities using their name (localName portion of IRI). Displays the local name extracted
   * from the entity's IRI, providing compact identifiers.
   */
  NAME,

  /**
   * Render entities using their CURIE or short-form ID. Uses prefix-based compact identifiers (e.g.,
   * "GO:0008150") when available.
   */
  ID,

  /**
   * Render entities using their full IRI. Displays complete IRIs, providing unambiguous entity
   * identification.
   */
  IRI,

  /**
   * Render entities using their rdfs:label annotation. Displays human-readable labels defined by
   * rdfs:label properties.
   */
  LABEL,
  ;
}
