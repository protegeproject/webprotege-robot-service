package edu.stanford.protege.robot.service.snapshot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import edu.stanford.protege.robot.service.exception.RobotServiceRuntimeException;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.revision.*;
import java.nio.file.Path;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;

/**
 * Focused tests for {@link ProjectOntologySnapshotProvider}.
 *
 * <p>
 * These tests cover the core behavior needed by the orchestrator: selecting an
 * ontology from a revision, retrying when the head revision is ahead, and
 * failing fast when the change history file is missing.
 */
class ProjectOntologySnapshotProviderTest {

  @TempDir
  Path tempDir;

  private RevisionManagerFactory revisionManagerFactory;

  private HeadRevisionNumberFinder headRevisionNumberFinder;

  private ChangeHistoryFileFactory changeHistoryFileFactory;

  private ProjectOntologySnapshotProvider snapshotProvider;

  @BeforeEach
  void setUp() {
    revisionManagerFactory = mock(RevisionManagerFactory.class);
    headRevisionNumberFinder = mock(HeadRevisionNumberFinder.class);
    changeHistoryFileFactory = mock(ChangeHistoryFileFactory.class);
    snapshotProvider = new ProjectOntologySnapshotProvider(revisionManagerFactory, headRevisionNumberFinder,
        changeHistoryFileFactory);
  }

  /**
   * Creates a snapshot from an available revision and returns the ontology
   * chosen by the provider's selection strategy.
   */
  @Test
  void createSnapshot_picksOntologyAndReturnsRevision() throws Exception {
    var projectId = ProjectId.generate();
    var historyFile = tempDir.resolve("changes.db").toFile();
    assertThat(historyFile.createNewFile()).isTrue();
    when(changeHistoryFileFactory.getChangeHistoryFile(projectId)).thenReturn(historyFile);

    var revisionManager = mock(RevisionManager.class);
    var revision = RevisionNumber.getRevisionNumber(3);
    when(revisionManager.getCurrentRevision()).thenReturn(revision);
    when(headRevisionNumberFinder.getHeadRevisionNumber(projectId)).thenReturn(revision);

    var manager = OWLManager.createOWLOntologyManager();
    var withoutIri = manager.createOntology();
    var withIri = manager.createOntology(IRI.create("http://example.org/ontology"));
    when(revisionManager.getOntologyManagerForRevision(revision)).thenReturn(manager);
    when(revisionManagerFactory.createRevisionManager(projectId)).thenReturn(revisionManager);

    var snapshot = snapshotProvider.createSnapshot(projectId);

    assertThat(snapshot.revisionNumber()).isEqualTo(3L);
    assertThat(Set.of(withoutIri, withIri)).contains(snapshot.ontology());
    assertThat(snapshot.ontology()).isSameAs(withoutIri);
  }

  /**
   * Retries the revision manager load when the head revision appears ahead,
   * then proceeds once the head matches.
   */
  @Test
  void createSnapshot_retriesWhenHeadRevisionAhead() throws Exception {
    var projectId = ProjectId.generate();
    var historyFile = tempDir.resolve("changes.db").toFile();
    assertThat(historyFile.createNewFile()).isTrue();
    when(changeHistoryFileFactory.getChangeHistoryFile(projectId)).thenReturn(historyFile);

    var revisionManager = mock(RevisionManager.class);
    var current = RevisionNumber.getRevisionNumber(3);
    when(revisionManager.getCurrentRevision()).thenReturn(current);
    when(revisionManagerFactory.createRevisionManager(projectId)).thenReturn(revisionManager);
    when(headRevisionNumberFinder.getHeadRevisionNumber(projectId))
        .thenReturn(RevisionNumber.getRevisionNumber(5))
        .thenReturn(current);

    var manager = OWLManager.createOWLOntologyManager();
    manager.createOntology();
    when(revisionManager.getOntologyManagerForRevision(current)).thenReturn(manager);

    var snapshot = snapshotProvider.createSnapshot(projectId);

    assertThat(snapshot.revisionNumber()).isEqualTo(3L);
    verify(revisionManagerFactory, times(2)).createRevisionManager(projectId);
    verify(headRevisionNumberFinder, times(2)).getHeadRevisionNumber(projectId);
  }

  /**
   * Fails fast when the change history file is missing.
   */
  @Test
  void createSnapshot_throwsWhenHistoryFileMissing() {
    var projectId = ProjectId.generate();
    var historyFile = tempDir.resolve("missing.db").toFile();
    when(changeHistoryFileFactory.getChangeHistoryFile(projectId)).thenReturn(historyFile);

    assertThatThrownBy(() -> snapshotProvider.createSnapshot(projectId))
        .isInstanceOf(RobotServiceRuntimeException.class)
        .hasMessageContaining("Change history file not found");
  }
}
