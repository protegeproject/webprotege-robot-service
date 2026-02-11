package edu.stanford.protege.robot.service.config;

import edu.stanford.protege.webprotege.revision.ChangeHistoryFileFactory;
import edu.stanford.protege.webprotege.revision.HeadRevisionNumberFinder;
import edu.stanford.protege.webprotege.revision.OntologyChangeRecordTranslator;
import edu.stanford.protege.webprotege.revision.OntologyChangeRecordTranslatorImpl;
import edu.stanford.protege.webprotege.revision.ProjectDirectoryFactory;
import edu.stanford.protege.webprotege.revision.RevisionManagerFactory;
import edu.stanford.protege.webprotege.revision.RevisionStoreFactory;
import java.io.File;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RevisionManagerConfiguration {

    @Bean
    OntologyChangeRecordTranslator ontologyChangeRecordTranslator() {
        return new OntologyChangeRecordTranslatorImpl();
    }

    @Bean
    ProjectDirectoryFactory projectDirectoryFactory(@Value("${webprotege.directories.data}") File dataDirectory) {
        return new ProjectDirectoryFactory(dataDirectory);
    }

    @Bean
    ChangeHistoryFileFactory changeHistoryFileFactory(ProjectDirectoryFactory projectDirectoryFactory) {
        return new ChangeHistoryFileFactory(projectDirectoryFactory);
    }

    @Bean
    RevisionStoreFactory revisionStoreFactory(ChangeHistoryFileFactory p1,
            OWLDataFactory p2,
            OntologyChangeRecordTranslator p3) {
        return new RevisionStoreFactory(p1, p2, p3);
    }

    @Bean
    RevisionManagerFactory revisionManagerFactory(RevisionStoreFactory revisionStoreFactory) {
        return new RevisionManagerFactory(revisionStoreFactory);
    }

    @Bean
    HeadRevisionNumberFinder headRevisionNumberFinder(ChangeHistoryFileFactory changeHistoryFileFactory) {
        return new HeadRevisionNumberFinder(changeHistoryFileFactory);
    }
}
