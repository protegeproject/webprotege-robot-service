package edu.stanford.protege.robot.command.merge;

/**
 * Boolean flags for ROBOT merge command.
 *
 * <p>
 * These flags control how multiple OWL ontologies are consolidated into one. The merge command
 * combines input ontologies, with options for handling import closures, annotations, and provenance
 * tracking.
 */
public enum MergeFlags {

    /**
     * Disable collapsing of import closure during merge.
     *
     * <p>
     * Generates {@code --collapse-import-closure false}. By default, ROBOT collapses the import
     * closure (merges all imported ontologies). When this flag is specified, imports are NOT collapsed,
     * keeping them as separate imports in the merged ontology.
     */
    NO_COLLAPSE_IMPORT_CLOSURE("--collapse-import-closure"),

    /**
     * Include ontology annotations in the merged result.
     *
     * <p>
     * Generates {@code --include-annotations true}. When specified, ontology-level annotations from
     * all input ontologies are included in the merged ontology.
     */
    INCLUDE_ANNOTATIONS("--include-annotations"),

    /**
     * Annotate merged axioms with their source ontology using derived-from.
     *
     * <p>
     * Generates {@code --annotate-derived-from true}. When specified, each axiom in the merged
     * ontology is annotated with the IRI of the source ontology it was derived from.
     */
    ANNOTATE_DERIVED_FROM("--annotate-derived-from"),

    /**
     * Annotate merged axioms with their defining ontology using defined-by.
     *
     * <p>
     * Generates {@code --annotate-defined-by true}. When specified, each axiom in the merged
     * ontology is annotated with the IRI of the ontology that defines it.
     */
    ANNOTATE_DEFINED_BY("--annotate-defined-by"),
    ;

    private final String flagName;

    MergeFlags(String flagName) {
        this.flagName = flagName;
    }

    /**
     * Returns the ROBOT CLI flag name.
     *
     * @return the flag string (e.g., {@code "--collapse-import-closure"})
     */
    public String getFlagName() {
        return flagName;
    }
}
