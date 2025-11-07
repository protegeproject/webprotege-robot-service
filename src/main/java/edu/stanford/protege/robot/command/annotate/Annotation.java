package edu.stanford.protege.robot.command.annotate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;

/**
 * Base interface for ROBOT annotation types.
 *
 * <p>
 * Annotations add metadata to ontologies using property-value pairs. Each implementation
 * converts to specific ROBOT command-line arguments.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({
    @JsonSubTypes.Type(PlainAnnotation.class),
    @JsonSubTypes.Type(TypedAnnotation.class),
    @JsonSubTypes.Type(LanguageAnnotation.class),
    @JsonSubTypes.Type(LinkAnnotation.class)})
public interface Annotation {

  /**
   * Returns the ROBOT CLI flag for this annotation type.
   *
   * @return the flag (e.g., {@code "--annotation"}, {@code "--typed-annotation"})
   */
  @JsonIgnore
  String getArgName();

  /**
   * Returns the annotation property IRI or prefixed name.
   *
   * @return property identifier (e.g., {@code "rdfs:comment"}, {@code
   *     "http://purl.org/dc/terms/license"})
   */
  String property();

  /**
   * Returns the annotation value.
   *
   * @return lexical value of the annotation
   */
  String value();

  /**
   * Converts this annotation to ROBOT command-line arguments.
   *
   * @return ordered list of CLI arguments including flag, property, value, and any additional
   *         parameters
   */
  @JsonIgnore
  List<String> getArgs();
}
