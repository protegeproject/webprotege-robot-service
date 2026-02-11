package edu.stanford.protege.robot.command.convert;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * RDF/XML format conversion convertStrategy for ROBOT convert command.
 *
 * <p>
 * The RDF/XML format (OWL) is the default serialization format for OWL ontologies and ROBOT. It
 * represents ontologies as RDF graphs serialized in XML. This format is widely supported by OWL
 * tools and reasoners.
 *
 * <p>
 * The RDF/XML format has no format-specific parameters and simply requires the format flag.
 */
@JsonTypeName("OWL")
public record OwlConvertStrategy() implements ConvertStrategy {

    public static final String FORMAT_FLAG = "--format";

    @Override
    public List<String> getArgs() {
        return ImmutableList.of(FORMAT_FLAG, ConvertFormat.owl.name());
    }
}
