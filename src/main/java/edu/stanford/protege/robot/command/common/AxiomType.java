package edu.stanford.protege.robot.command.common;

/**
 * Axiom types for ROBOT filter and remove commands.
 *
 * <p>
 * Specifies which categories of axioms to consider during filtering or removal. Common shortcuts
 * like
 * {@code all}, {@code logical}, and {@code annotation} are provided, along with specific axiom
 * types
 * and special selectors for advanced filtering.
 *
 * @see <a href="https://robot.obolibrary.org/filter">ROBOT Filter Documentation</a>
 * @see <a href="https://robot.obolibrary.org/remove">ROBOT Remove Documentation</a>
 */
public enum AxiomType {

  /**
   * All axiom types. Shortcut that selects all axioms in the ontology for potential filtering or
   * removal.
   */
  all,

  /**
   * All logical axioms. Shortcut that selects logical axioms (SubClassOf, EquivalentClasses,
   * DisjointClasses, etc.)
   * while excluding annotation axioms.
   */
  logical,

  /**
   * All annotation axioms. Shortcut that selects annotation axioms (entity annotations, axiom
   * annotations) while
   * excluding logical axioms.
   */
  annotation,

  /**
   * SubClassOf axioms. Selects only SubClassOf axioms that define class hierarchy relationships.
   */
  subclass,

  /**
   * SubPropertyOf axioms. Selects only SubPropertyOf axioms that define property hierarchy
   * relationships.
   */
  subproperty,

  /**
   * Equivalence axioms. Selects axioms declaring equivalence between classes, properties, or
   * individuals
   * (EquivalentClasses, EquivalentProperties).
   */
  equivalent,

  /**
   * Disjointness axioms. Selects axioms declaring disjointness between classes, properties, or
   * individuals
   * (DisjointClasses, DisjointProperties).
   */
  disjoint,

  /**
   * ClassAssertion axioms. Selects axioms asserting that an individual is an instance of a class.
   */
  type,

  /**
   * TBox axioms (terminological). Selects terminological axioms that describe the structure of the
   * domain (class and property
   * definitions).
   */
  tbox,

  /**
   * ABox axioms (assertional). Selects assertional axioms that describe individuals and their
   * relationships.
   */
  abox,

  /**
   * RBox axioms (property). Selects axioms describing property characteristics and relationships.
   */
  rbox,

  /**
   * Axioms with base IRI entities only. Selects axioms where all referenced entities use the base IRI
   * namespace. Requires {@code
   * --base-iri} to be specified.
   */
  internal,

  /**
   * Axioms with non-base IRI entities. Selects axioms where at least one referenced entity does not
   * use the base IRI namespace.
   * Requires {@code --base-iri} to be specified.
   */
  external,

  /**
   * Tautological axioms. Selects axioms that are logically tautological (always true). Warning: This
   * may select more
   * axioms than desired as it uses broad criteria for identifying tautologies.
   */
  tautologies,

  /**
   * Structural tautologies only. Selects only structurally tautological axioms using more
   * conservative criteria than {@code
   * tautologies}. This is safer for most use cases.
   */
  structural_tautologies;
}
