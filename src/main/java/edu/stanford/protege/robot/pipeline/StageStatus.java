package edu.stanford.protege.robot.pipeline;

/**
 * Represents the execution status of a single pipeline stage.
 */
public enum StageStatus {
    /**
     * The stage is waiting to be executed.
     */
    WAITING,

    /**
     * The stage is currently running.
     */
    RUNNING,

    /**
     * The stage finished successfully without errors.
     */
    FINISHED_WITH_SUCCESS,

    /**
     * The stage finished with one or more errors.
     */
    FINISHED_WITH_ERROR
}
