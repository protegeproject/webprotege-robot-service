package edu.stanford.protege.robot.pipeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import edu.stanford.protege.webprotege.common.ValueObject;
import java.util.UUID;
import javax.annotation.Nonnull;

/**
 * Unique identifier for a pipeline stage.
 */
public record PipelineStageId(String id) implements ValueObject {

  @JsonCreator
  @Nonnull
  public static PipelineStageId valueOf(String id) {
    return new PipelineStageId(UUID.fromString(id).toString());
  }

  @Nonnull
  public static PipelineStageId generate() {
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
