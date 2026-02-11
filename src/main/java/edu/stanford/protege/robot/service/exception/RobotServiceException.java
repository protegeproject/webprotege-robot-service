package edu.stanford.protege.robot.service.exception;

public class RobotServiceException extends Exception {

    public RobotServiceException(String message) {
        super(message);
    }

    public RobotServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
