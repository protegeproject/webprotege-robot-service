package edu.stanford.protege.robot.command.common;

/**
 * Supported OWL reasoners for ROBOT commands.
 *
 * <p>
 * Many ROBOT commands require a reasoner for tasks like consistency checking, classification,
 * and entailment detection. Different reasoners support different OWL profiles and have
 * different performance characteristics.
 *
 * @see <a href="https://robot.obolibrary.org/reason">ROBOT Reason Documentation</a>
 */
public enum Reasoner {

    /**
     * ELK reasoner for OWL EL ontologies.
     *
     * <p>
     * Fast reasoner optimized for the OWL EL profile. Suitable for most biomedical ontologies
     * that use only existential restrictions and conjunctions.
     */
    ELK("ELK"),

    /**
     * HermiT reasoner for full OWL DL ontologies.
     *
     * <p>
     * Complete reasoner supporting the full OWL DL profile, including universal restrictions,
     * cardinality constraints, and nominals. Slower than ELK but handles more expressive ontologies.
     */
    HERMIT("HermiT"),

    /**
     * JFact reasoner for OWL DL ontologies.
     *
     * <p>
     * Alternative OWL DL reasoner. Supports the same expressivity as HermiT with different
     * performance characteristics.
     */
    JFACT("JFact"),

    /**
     * Whelk reasoner for OWL EL ontologies.
     *
     * <p>
     * Scala-based EL reasoner. Alternative to ELK for OWL EL profile ontologies.
     */
    WHELK("whelk"),

    /**
     * Structural reasoner using syntactic checks only.
     *
     * <p>
     * Lightweight reasoner that performs structural (syntactic) subsumption checks without
     * full logical reasoning. Fastest option but may miss logically entailed redundancies.
     */
    STRUCTURAL("structural");

    private final String reasonerName;

    Reasoner(String reasonerName) {
        this.reasonerName = reasonerName;
    }

    /**
     * Returns the ROBOT CLI reasoner name.
     *
     * @return the reasoner name string (e.g., {@code "ELK"}, {@code "HermiT"})
     */
    public String getReasonerName() {
        return reasonerName;
    }
}
