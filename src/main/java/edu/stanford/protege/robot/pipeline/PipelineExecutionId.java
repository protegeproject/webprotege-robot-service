package edu.stanford.protege.robot.pipeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import edu.stanford.protege.webprotege.common.ValueObject;
import java.util.UUID;
import javax.annotation.Nonnull;

/**
 * Unique identifier for a pipeline execution process.
 */
public record PipelineExecutionId(String id) implements ValueObject {

  @JsonCreator
  @Nonnull
  public static PipelineExecutionId valueOf(String id) {
    return new PipelineExecutionId(UUID.fromString(id).toString());
  }

  @Nonnull
  public static PipelineExecutionId generate() {
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
