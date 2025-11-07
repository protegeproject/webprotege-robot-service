package edu.stanford.protege.robot.pipeline;

import java.util.UUID;

public record PipelineId(String id) {

  public static PipelineId generate() {
    return new PipelineId(UUID.randomUUID().toString());
  }
}
