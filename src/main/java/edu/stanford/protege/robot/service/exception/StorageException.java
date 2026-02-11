package edu.stanford.protege.robot.service.exception;

public class StorageException extends RobotServiceRuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
