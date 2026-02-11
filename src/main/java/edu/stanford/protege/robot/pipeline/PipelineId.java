package edu.stanford.protege.robot.pipeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import edu.stanford.protege.webprotege.common.ValueObject;
import java.util.UUID;
import javax.annotation.Nonnull;

/**
 * Unique identifier for a ROBOT pipeline.
 */
public record PipelineId(@Nonnull String id) implements ValueObject {

    @JsonCreator
    @Nonnull
    public static PipelineId valueOf(String id) {
        return new PipelineId(UUID.fromString(id).toString());
    }

    @Nonnull
    public static PipelineId generate() {
        return valueOf(UUID.randomUUID().toString());
    }

    @JsonValue
    @Override
    public String id() {
        return id;
    }

    @Override
    public String value() {
        return id;
    }
}
