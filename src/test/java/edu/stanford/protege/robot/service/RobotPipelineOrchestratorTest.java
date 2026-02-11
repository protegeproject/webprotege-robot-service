package edu.stanford.protege.robot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import edu.stanford.protege.robot.pipeline.*;
import edu.stanford.protege.robot.service.snapshot.ProjectOntologySnapshot;
import edu.stanford.protege.robot.service.snapshot.ProjectOntologySnapshotProvider;
import edu.stanford.protege.webprotege.common.ProjectId;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.semanticweb.owlapi.apibinding.OWLManager;

/**
 * Focused tests for {@link RobotPipelineOrchestrator} behavior.
 *
 * <p>
 * These tests run the orchestrator with a direct executor to make the async path
 * synchronous and deterministic. That lets us assert the preparation-status
 * transitions and verify snapshot success/failure wiring without involving
 * real infrastructure.
 */
@ExtendWith(MockitoExtension.class)
class RobotPipelineOrchestratorTest {

    @Mock
    private RobotPipelineExecutor executor;

    @Mock
    private ProjectOntologySnapshotProvider snapshotProvider;

    @Mock
    private PipelineStatusRepository statusRepository;

    @Mock
    private PipelineLogger pipelineLogger;

    private RobotPipelineOrchestrator orchestrator;

    private AtomicReference<PipelineStatus> lastStatus;

    @BeforeEach
    void setUp() {
        lastStatus = new AtomicReference<>();
        when(statusRepository.findStatus(any()))
                .thenAnswer(invocation -> Optional.ofNullable(lastStatus.get()));
        doAnswer(invocation -> {
            lastStatus.set(invocation.getArgument(0));
            return null;
        }).when(statusRepository).saveStatus(any());
        Executor directExecutor = Runnable::run;
        orchestrator = new RobotPipelineOrchestrator(executor, snapshotProvider, statusRepository, pipelineLogger,
                directExecutor);
    }

    /**
     * Snapshot succeeds: preparation status transitions to success, and the
     * pipeline executor is invoked with the snapshot ontology.
     */
    @Test
    void executeAsync_snapshotSucceeds_updatesPreparationAndInvokesExecutor() throws Exception {
        var projectId = ProjectId.generate();
        var pipeline = new RobotPipeline(projectId, PipelineId.generate(), null, null, List.of());
        var ontology = OWLManager.createOWLOntologyManager().createOntology();
        when(snapshotProvider.createSnapshot(projectId)).thenReturn(new ProjectOntologySnapshot(ontology, 7L));

        var executionId = orchestrator.executeAsync(projectId, pipeline);

        assertThat(executionId).isNotNull();
        verify(pipelineLogger).snapshotOntologyStarted(projectId, executionId, pipeline.pipelineId());
        verify(pipelineLogger).snapshotOntologySucceeded(projectId, executionId, pipeline.pipelineId());
        verify(executor).executePipeline(projectId, executionId, ontology, 7L, pipeline);

        var statusCaptor = ArgumentCaptor.forClass(PipelineStatus.class);
        verify(statusRepository, atLeast(3)).saveStatus(statusCaptor.capture());
        var savedStatuses = statusCaptor.getAllValues();

        assertThat(savedStatuses.get(0).preparationStatus())
                .isEqualTo(PipelinePreparationStatus.waiting("Preparing ontology snapshot"));
        assertThat(savedStatuses.get(1).preparationStatus())
                .isEqualTo(PipelinePreparationStatus.running("Preparing ontology snapshot"));
        assertThat(savedStatuses.get(savedStatuses.size() - 1).preparationStatus())
                .isEqualTo(PipelinePreparationStatus.finishedWithSuccess("Ontology snapshot ready"));
    }

    /**
     * Snapshot fails: preparation status is marked failed and ended,
     * and the pipeline executor is not invoked.
     */
    @Test
    void executeAsync_snapshotFails_updatesPreparationAndDoesNotInvokeExecutor() {
        var projectId = ProjectId.generate();
        var pipeline = new RobotPipeline(projectId, PipelineId.generate(), null, null, List.of());
        when(snapshotProvider.createSnapshot(projectId)).thenThrow(new RuntimeException("deliberate failure"));

        var executionId = orchestrator.executeAsync(projectId, pipeline);

        assertThat(executionId).isNotNull();
        verify(pipelineLogger).snapshotOntologyStarted(projectId, executionId, pipeline.pipelineId());
        verify(pipelineLogger).snapshotOntologyFailed(eq(projectId), eq(executionId), eq(pipeline.pipelineId()), any());
        verify(pipelineLogger).pipelineExecutionFinishedWithError(eq(projectId), eq(executionId),
                eq(pipeline.pipelineId()),
                any());
        verify(executor, never()).executePipeline(any(), any(), any(), anyLong(), any());

        var statusCaptor = ArgumentCaptor.forClass(PipelineStatus.class);
        verify(statusRepository, atLeast(3)).saveStatus(statusCaptor.capture());
        var terminalStatus = statusCaptor.getAllValues()
                .get(statusCaptor.getAllValues().size() - 1);
        assertThat(terminalStatus.preparationStatus().status())
                .isEqualTo(PipelinePreparationStatus.finishedWithError("Snapshot failed: deliberate failure").status());
        assertThat(terminalStatus.endTime()).isNotNull();
    }
}
