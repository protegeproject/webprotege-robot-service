package edu.stanford.protege.robot.command.convert;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * OWL Functional Syntax format conversion convertStrategy for ROBOT convert command.
 *
 * <p>
 * The OWL Functional Syntax (OFN) is a compact, human-readable syntax for OWL 2 ontologies. It
 * uses a functional-style notation that is easy to parse and commonly used in formal
 * specifications and documentation.
 *
 * <p>
 * The OFN format has no format-specific parameters and simply requires the format flag.
 */
@JsonTypeName("OFN")
public record OfnConvertStrategy() implements ConvertStrategy {

    public static final String FORMAT_FLAG = "--format";

    @Override
    public List<String> getArgs() {
        return ImmutableList.of(FORMAT_FLAG, ConvertFormat.ofn.name());
    }
}
