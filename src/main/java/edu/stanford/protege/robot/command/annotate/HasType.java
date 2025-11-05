package edu.stanford.protege.robot.command.annotate;

/**
 * Marker interface for annotations with explicit datatypes.
 *
 * <p>
 * Indicates that an annotation includes an XSD datatype specification.
 */
public interface HasType {

  /**
   * Returns the XSD datatype IRI or prefixed name.
   *
   * @return datatype identifier (e.g., "xsd:integer", "xsd:dateTime")
   */
  String type();
}
