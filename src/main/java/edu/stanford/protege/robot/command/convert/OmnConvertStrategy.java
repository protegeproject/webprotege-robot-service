package edu.stanford.protege.robot.command.convert;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * Manchester Syntax format conversion convertStrategy for ROBOT convert command.
 *
 * <p>
 * The Manchester Syntax (OMN) is a compact, user-friendly syntax for OWL 2 ontologies designed to
 * be more readable than RDF/XML. It is commonly used in Protégé and other ontology editing tools
 * for its intuitive, English-like notation.
 *
 * <p>
 * The Manchester Syntax format has no format-specific parameters and simply requires the format
 * flag.
 */
@JsonTypeName("OMN")
public record OmnConvertStrategy() implements ConvertStrategy {

  public static final String FORMAT_FLAG = "--format";

  @Override
  public List<String> getArgs() {
    return ImmutableList.of(FORMAT_FLAG, ConvertFormat.omn.name());
  }
}
