package edu.stanford.protege.robot.service;

import edu.stanford.protege.robot.pipeline.PipelineExecutionId;
import edu.stanford.protege.robot.pipeline.PipelineLogger;
import edu.stanford.protege.robot.pipeline.RobotPipeline;
import edu.stanford.protege.robot.service.exception.RobotServiceException;
import edu.stanford.protege.webprotege.common.ProjectId;
import java.nio.file.Path;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Provider;
import org.obolibrary.robot.CommandState;
import org.obolibrary.robot.IOHelper;
import org.springframework.stereotype.Service;

/**
 * Spring service for executing chains of ROBOT commands sequentially, threading state between
 * commands.
 *
 * <p>
 * This executor enables programmatic composition of ROBOT command pipelines. Each command
 * receives the output from the previous command via CommandState, creating processing pipelines
 * without intermediate file I/O.
 */
@Service
public class RobotPipelineExecutor {

  private final Provider<CommandState> commandStateProvider;
  private final IOHelper ioHelper;

  /**
   * Creates a new RobotPipelineExecutor with Spring-injected dependencies.
   *
   * @param commandStateProvider
   *          provider for CommandState instances
   * @param ioHelper
   *          helper for ontology I/O operations
   */
  public RobotPipelineExecutor(
      Provider<CommandState> commandStateProvider, IOHelper ioHelper) {
    this.commandStateProvider = commandStateProvider;
    this.ioHelper = ioHelper;
  }

  /**
   * Executes a chain of ROBOT commands sequentially.
   *
   * <p>
   * Commands receive the output from the previous command automatically. The first command receives
   * the loaded input ontology, and subsequent commands receive the modified ontology from their
   * predecessor.
   *
   * @param ontologyFilePath
   *          path to the input ontology file (required)
   * @param pipeline
   *          the ROBOT pipeline containing the sequence of commands to execute
   * @param pipelineLogger
   *          logger for tracking pipeline execution progress and events
   * @param outputPath
   *          path to save the final ontology (required)
   * @return the final CommandState after all commands have executed
   * @throws RobotServiceException
   *           if any command fails or I/O error occurs
   */
  public CommandState executePipeline(@Nonnull Path ontologyFilePath,
      @Nonnull ProjectId projectId,
      @Nonnull RobotPipeline pipeline,
      @Nonnull PipelineLogger pipelineLogger,
      @Nonnull Path outputPath) throws RobotServiceException {

    // Validate inputs
    Objects.requireNonNull(ontologyFilePath, "ontologyFilePath cannot be null");
    Objects.requireNonNull(pipeline, "pipeline cannot be null");
    Objects.requireNonNull(pipelineLogger, "pipelineLogger cannot be null");
    Objects.requireNonNull(outputPath, "outputPath cannot be null");

    var executionId = PipelineExecutionId.generate();

    try {
      pipelineLogger.pipelineExecutionStarted(projectId, executionId, pipeline);

      // Get fresh CommandState
      var state = commandStateProvider.get();

      // Load input ontology
      try {
        pipelineLogger.loadingOntologyStarted(projectId, executionId, pipeline);
        var ontology = ioHelper.loadOntology(ontologyFilePath.toFile());
        pipelineLogger.loadingOntologySucceeded(projectId, executionId, pipeline);
        state.setOntology(ontology);
        state.setOntologyPath(ontologyFilePath.toString());
      } catch (Throwable t) {
        pipelineLogger.loadingOntologyFailed(projectId, executionId, pipeline, t);
        throw new RobotServiceException("Error loading ontology: " + t.getMessage(), t);
      }

      // Execute each command sequentially, threading state between them
      for (var pipelineStage : pipeline.stages()) {
        var robotCommand = pipelineStage.command();
        var command = robotCommand.getCommand();
        var args = robotCommand.getArgsArray();
        try {
          pipelineLogger.pipelineStageRunStarted(projectId, executionId, pipeline, pipelineStage);
          state = command.execute(state, args);
          pipelineLogger.pipelineStageRunFinished(projectId, executionId, pipeline, pipelineStage);
        } catch (Throwable t) {
          pipelineLogger.pipelineStageFailed(projectId, executionId, pipeline, pipelineStage, t);
          throw new RobotServiceException("Error executing pipeline stage: " + t.getMessage(), t);
        }

        // Verify state is not null after execution
        if (state == null) {
          throw new RobotServiceException("Command " + command.getName() + " returned null state");
        }
      }

      // Save output
      try {
        pipelineLogger.savingOntologyStarted(projectId, executionId, pipeline);
        ioHelper.saveOntology(state.getOntology(), outputPath.toString());
        pipelineLogger.savingOntologySucceeded(projectId, executionId, pipeline);
      } catch (Throwable t) {
        pipelineLogger.savingOntologyFailed(projectId, executionId, pipeline, t);
        throw new RobotServiceException("Error saving ontology: " + t.getMessage(), t);
      }

      return state;
    } finally {
      pipelineLogger.pipelineExecutionFinished(projectId, executionId, pipeline);
    }
  }
}
