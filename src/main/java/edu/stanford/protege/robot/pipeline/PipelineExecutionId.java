package edu.stanford.protege.robot.pipeline;

import java.util.UUID;

public record PipelineExecutionId(String id) {

  public static PipelineExecutionId generate() {
    return new PipelineExecutionId(UUID.randomUUID().toString());
  }
}
