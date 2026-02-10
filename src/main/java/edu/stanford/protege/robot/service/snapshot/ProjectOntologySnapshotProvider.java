package edu.stanford.protege.robot.service.snapshot;

import edu.stanford.protege.robot.service.exception.RobotServiceRuntimeException;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.revision.ChangeHistoryFileFactory;
import edu.stanford.protege.webprotege.revision.HeadRevisionNumberFinder;
import edu.stanford.protege.webprotege.revision.RevisionManager;
import edu.stanford.protege.webprotege.revision.RevisionManagerFactory;
import edu.stanford.protege.webprotege.revision.RevisionNumber;
import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProjectOntologySnapshotProvider {

  private static final Logger logger = LoggerFactory.getLogger(ProjectOntologySnapshotProvider.class);

  private static final int MAX_LOAD_ATTEMPTS = 3;
  private static final long LOAD_RETRY_DELAY_MS = 250;

  private final RevisionManagerFactory revisionManagerFactory;
  private final HeadRevisionNumberFinder headRevisionNumberFinder;
  private final ChangeHistoryFileFactory changeHistoryFileFactory;

  public ProjectOntologySnapshotProvider(@Nonnull RevisionManagerFactory revisionManagerFactory,
      @Nonnull HeadRevisionNumberFinder headRevisionNumberFinder,
      @Nonnull ChangeHistoryFileFactory changeHistoryFileFactory) {
    this.revisionManagerFactory = revisionManagerFactory;
    this.headRevisionNumberFinder = headRevisionNumberFinder;
    this.changeHistoryFileFactory = changeHistoryFileFactory;
  }

  public ProjectOntologySnapshot createSnapshot(@Nonnull ProjectId projectId) {
    var changeHistoryFile = changeHistoryFileFactory.getChangeHistoryFile(projectId);
    if (!changeHistoryFile.exists()) {
      throw new RobotServiceRuntimeException("Change history file not found for project " + projectId
          + " at " + changeHistoryFile.getAbsolutePath());
    }

    var revisionManager = loadRevisionManagerWithRetry(projectId);
    var revisionNumber = revisionManager.getCurrentRevision();
    var ontologyManager = revisionManager.getOntologyManagerForRevision(revisionNumber);
    var ontology = selectOntology(ontologyManager)
        .orElseThrow(() -> new RobotServiceRuntimeException("No ontology found after loading revisions for "
            + projectId));
    return new ProjectOntologySnapshot(ontology, revisionNumber.getValue());
  }

  private RevisionManager loadRevisionManagerWithRetry(ProjectId projectId) {
    RevisionManager revisionManager = null;
    RevisionNumber headRevision = null;

    for (int attempt = 1; attempt <= MAX_LOAD_ATTEMPTS; attempt++) {
      revisionManager = revisionManagerFactory.createRevisionManager(projectId);
      var currentRevision = revisionManager.getCurrentRevision();

      try {
        headRevision = headRevisionNumberFinder.getHeadRevisionNumber(projectId);
      } catch (IOException e) {
        logger.warn("{} Unable to read head revision number: {}", projectId, e.getMessage());
      }

      if (headRevision == null || currentRevision.compareTo(headRevision) >= 0) {
        return revisionManager;
      }

      logger.warn("{} Loaded revision {} but head appears to be {}. Retrying load (attempt {}/{})",
          projectId, currentRevision.getValue(), headRevision.getValue(), attempt, MAX_LOAD_ATTEMPTS);
      sleepBeforeRetry();
    }

    logger.warn("{} Proceeding with revision {} (head was {}) after retries",
        projectId,
        revisionManager.getCurrentRevision().getValue(),
        headRevision == null ? "unknown" : headRevision.getValue());
    return revisionManager;
  }

  private void sleepBeforeRetry() {
    try {
      Thread.sleep(LOAD_RETRY_DELAY_MS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private Optional<OWLOntology> selectOntology(OWLOntologyManager ontologyManager) {
    if (ontologyManager.getOntologies().isEmpty()) {
      return Optional.empty();
    }
    return ontologyManager.getOntologies().stream()
        .sorted(Comparator.comparing(o -> o.getOntologyID().getOntologyIRI().isPresent()))
        .findFirst();
  }

}
