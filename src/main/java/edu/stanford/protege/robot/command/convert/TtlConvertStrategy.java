package edu.stanford.protege.robot.command.convert;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * Turtle format conversion convertStrategy for ROBOT convert command.
 *
 * <p>
 * The Turtle format (TTL) is a compact, human-readable RDF serialization format. It uses a simple
 * triple-based syntax that is easier to read and write than RDF/XML, making it popular for manual
 * editing and version control.
 *
 * <p>
 * The Turtle format has no format-specific parameters and simply requires the format flag.
 */
@JsonTypeName("TTL")
public record TtlConvertStrategy() implements ConvertStrategy {

  public static final String FORMAT_FLAG = "--format";

  @Override
  public List<String> getArgs() {
    return ImmutableList.of(FORMAT_FLAG, ConvertFormat.ttl.name());
  }
}
