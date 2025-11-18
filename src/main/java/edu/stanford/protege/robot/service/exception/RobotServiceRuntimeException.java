package edu.stanford.protege.robot.service.exception;

public class RobotServiceRuntimeException extends RuntimeException {

  public RobotServiceRuntimeException(String message) {
    super(message);
  }

  public RobotServiceRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}
