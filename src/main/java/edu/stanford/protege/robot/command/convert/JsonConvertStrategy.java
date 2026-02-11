package edu.stanford.protege.robot.command.convert;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * OBO Graphs JSON format conversion convertStrategy for ROBOT convert command.
 *
 * <p>
 * The JSON format outputs ontologies in the OBO Graphs JSON format, which provides a standardized
 * JSON representation of ontology structures. This format is particularly useful for web
 * applications and JavaScript-based tools.
 *
 * <p>
 * The JSON format has no format-specific parameters and simply requires the format flag.
 */
@JsonTypeName("JSON")
public record JsonConvertStrategy() implements ConvertStrategy {

    public static final String FORMAT_FLAG = "--format";

    @Override
    public List<String> getArgs() {
        return ImmutableList.of(FORMAT_FLAG, ConvertFormat.json.name());
    }
}
