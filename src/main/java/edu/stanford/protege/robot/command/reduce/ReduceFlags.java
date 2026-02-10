package edu.stanford.protege.robot.command.reduce;

/**
 * Boolean flags for ROBOT reduce command.
 *
 * <p>
 * These flags control how redundant subClassOf axioms are identified and removed. The reduce
 * command uses automated reasoning to find axioms that are entailed by other axioms in the
 * ontology, and removes them to simplify the class hierarchy.
 */
public enum ReduceFlags {

  /**
   * Preserve axioms that have annotations.
   *
   * <p>
   * Generates {@code --preserve-annotated-axioms true}. When specified, subClassOf axioms that
   * carry annotations (such as provenance or definition source) are retained even if they are
   * logically redundant.
   */
  PRESERVE_ANNOTATED_AXIOMS("--preserve-annotated-axioms"),

  /**
   * Check only named classes for redundancy.
   *
   * <p>
   * Generates {@code --named-classes-only true}. When specified, restricts redundancy checking
   * to named classes only, excluding anonymous class expressions from the evaluation.
   */
  NAMED_CLASSES_ONLY("--named-classes-only"),

  /**
   * Include subproperties in redundancy evaluation.
   *
   * <p>
   * Generates {@code --include-subproperties true}. When specified, factors subproperty
   * relationships into the redundancy evaluation, allowing the reasoner to detect additional
   * redundant axioms through property hierarchy reasoning.
   */
  INCLUDE_SUBPROPERTIES("--include-subproperties"),
  ;

  private final String flagName;

  ReduceFlags(String flagName) {
    this.flagName = flagName;
  }

  /**
   * Returns the ROBOT CLI flag name.
   *
   * @return the flag string (e.g., {@code "--preserve-annotated-axioms"})
   */
  public String getFlagName() {
    return flagName;
  }
}
