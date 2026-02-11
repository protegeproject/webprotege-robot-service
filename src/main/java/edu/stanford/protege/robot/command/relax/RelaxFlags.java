package edu.stanford.protege.robot.command.relax;

/**
 * Boolean flags for ROBOT relax command.
 *
 * <p>
 * These flags control how equivalence axioms and complex expressions are relaxed into simpler
 * SubClassOf axioms. The relax command converts OWL EquivalenceAxioms into weaker SubClassOf
 * axioms for applications that only consume SubClassOf relationships.
 */
public enum RelaxFlags {

    /**
     * Include named class equivalences in relaxation.
     *
     * <p>
     * Generates {@code --exclude-named-classes false}. By default, equivalence axioms between two
     * named classes (e.g., {@code :A EquivalentTo :B}) are NOT relaxed. When this flag is specified,
     * such axioms are converted to SubClassOf statements.
     */
    INCLUDE_NAMED_CLASSES("--exclude-named-classes"),

    /**
     * Extend relaxation to SubClassOf axioms with conjunctive expressions.
     *
     * <p>
     * Generates {@code --include-subclass-of true}. When specified, breaks down complex SubClassOf
     * statements like {@code finger SubClassOf digit and 'part of' some hand} into separate axioms:
     * {@code finger SubClassOf digit} and {@code finger SubClassOf 'part of' some hand}.
     */
    INCLUDE_SUBCLASS_OF("--include-subclass-of"),

    /**
     * Restrict output to simple existential restrictions compatible with OBO format.
     *
     * <p>
     * Generates {@code --enforce-obo-format true}. When specified, filters out complex nested
     * expressions during relaxation, ensuring the output conforms to OBO format restrictions.
     */
    ENFORCE_OBO_FORMAT("--enforce-obo-format"),
    ;

    private final String flagName;

    RelaxFlags(String flagName) {
        this.flagName = flagName;
    }

    /**
     * Returns the ROBOT CLI flag name.
     *
     * @return the flag string (e.g., {@code "--exclude-named-classes"})
     */
    public String getFlagName() {
        return flagName;
    }
}
