package edu.stanford.protege.robot.command.remove;

/**
 * Predefined selectors for ROBOT remove command.
 *
 * <p>
 * Selectors filter or expand the target term set. This enum provides well-defined subset and
 * relation selectors. For pattern selectors (e.g., {@code CURIE=CURIE} or {@code
 * <IRI-pattern>}), use custom strings instead of enum values.
 *
 * <p>
 * Subset selectors filter the target set by entity type, while relation selectors navigate the
 * ontology graph to expand the target set based on relationships.
 */
public enum RemoveSelector {

  /**
   * Filter by OWL classes. Subset selector that restricts the target set to only include class
   * entities.
   */
  classes,

  /**
   * All property types. Subset selector that includes object properties, data properties, and
   * annotation properties.
   */
  properties,

  /**
   * Object properties only. Subset selector that restricts the target set to object property
   * entities.
   */
  object_properties,

  /**
   * Data properties only. Subset selector that restricts the target set to data property entities.
   */
  data_properties,

  /**
   * Annotation properties only. Subset selector that restricts the target set to annotation property
   * entities.
   */
  annotation_properties,

  /**
   * Named individuals. Subset selector that restricts the target set to named individual entities.
   */
  individuals,

  /**
   * Named entities. Subset selector that restricts the target set to entities with named IRIs
   * (excludes anonymous
   * entities).
   */
  named,

  /**
   * Anonymous entities. Subset selector that restricts the target set to anonymous entities (excludes
   * named entities).
   */
  anonymous,

  /**
   * The term itself. Relation selector that includes only the explicitly specified terms without
   * expansion.
   */
  self,

  /**
   * Direct superclasses. Relation selector that expands the target set to include immediate parent
   * classes.
   */
  parents,

  /**
   * All superclasses (transitive). Relation selector that expands the target set to include all
   * ancestor classes in the
   * hierarchy.
   */
  ancestors,

  /**
   * Direct subclasses. Relation selector that expands the target set to include immediate child
   * classes.
   */
  children,

  /**
   * All subclasses (transitive). Relation selector that expands the target set to include all
   * descendant classes in the
   * hierarchy.
   */
  descendants,

  /**
   * Equivalent classes. Relation selector that expands the target set to include classes declared
   * equivalent to the
   * target terms.
   */
  equivalents,

  /**
   * Types of individuals. Relation selector that expands the target set to include the class types of
   * individual
   * entities.
   */
  types,

  /**
   * Instances of classes. Relation selector that expands the target set to include individual
   * instances of the target
   * classes.
   */
  instances,

  /**
   * Domains of properties. Relation selector that expands the target set to include the domain
   * classes of the target
   * properties.
   */
  domains,

  /**
   * Ranges of properties. Relation selector that expands the target set to include the range classes
   * of the target
   * properties.
   */
  ranges,

  /**
   * All terms NOT in selection. Relation selector that inverts the target set to include all entities
   * except those currently
   * selected.
   */
  complement,

  /**
   * Terms defined in ontology. Relation selector that restricts the target set to terms defined in
   * the main ontology
   * (excludes imported terms).
   */
  ontology,

  /**
   * Terms from imported ontologies. Relation selector that restricts the target set to terms defined
   * in imported ontologies.
   */
  imports;
}
