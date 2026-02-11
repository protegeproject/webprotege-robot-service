package edu.stanford.protege.robot.command.convert;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * OWL/XML format conversion convertStrategy for ROBOT convert command.
 *
 * <p>
 * The OWL/XML format (OWX) is an alternative XML serialization for OWL 2 ontologies that is more
 * aligned with the OWL 2 structural specification. It provides a cleaner XML structure compared to
 * RDF/XML and is easier to process with standard XML tools.
 *
 * <p>
 * The OWL/XML format has no format-specific parameters and simply requires the format flag.
 */
@JsonTypeName("OWX")
public record OwxConvertStrategy() implements ConvertStrategy {

    public static final String FORMAT_FLAG = "--format";

    @Override
    public List<String> getArgs() {
        return ImmutableList.of(FORMAT_FLAG, ConvertFormat.owx.name());
    }
}
