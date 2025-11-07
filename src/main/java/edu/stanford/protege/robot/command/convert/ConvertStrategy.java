package edu.stanford.protege.robot.command.convert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;

/**
 * Base interface for ROBOT convert format strategies.
 *
 * <p>
 * Convert strategies encapsulate format-specific parameters and options for ontology format
 * conversion. Each implementation represents a target format and its configuration options.
 *
 * <p>
 * Implementations include:
 *
 * <ul>
 * <li>{@link OboConvertStrategy} - OBO format with validation and cleaning options</li>
 * <li>{@link JsonConvertStrategy} - OBO Graphs JSON format</li>
 * <li>{@link OfnConvertStrategy} - OWL Functional Syntax</li>
 * <li>{@link OmnConvertStrategy} - Manchester Syntax</li>
 * <li>{@link OwlConvertStrategy} - RDF/XML format</li>
 * <li>{@link OwxConvertStrategy} - OWL/XML format</li>
 * <li>{@link TtlConvertStrategy} - Turtle format</li>
 * </ul>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({
    @JsonSubTypes.Type(OboConvertStrategy.class),
    @JsonSubTypes.Type(JsonConvertStrategy.class),
    @JsonSubTypes.Type(OfnConvertStrategy.class),
    @JsonSubTypes.Type(OmnConvertStrategy.class),
    @JsonSubTypes.Type(OwlConvertStrategy.class),
    @JsonSubTypes.Type(OwxConvertStrategy.class),
    @JsonSubTypes.Type(TtlConvertStrategy.class)})
public interface ConvertStrategy {

  /**
   * Converts this convert convertStrategy to command-line arguments for ROBOT.
   *
   * @return immutable list of CLI arguments for this conversion format
   */
  @JsonIgnore
  List<String> getArgs();
}
