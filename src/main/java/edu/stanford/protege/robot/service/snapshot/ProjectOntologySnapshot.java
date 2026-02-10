package edu.stanford.protege.robot.service.snapshot;

import org.semanticweb.owlapi.model.OWLOntology;

public record ProjectOntologySnapshot(OWLOntology ontology, long revisionNumber) {
}
