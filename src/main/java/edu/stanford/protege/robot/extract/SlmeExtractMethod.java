package edu.stanford.protege.robot.extract;

/**
 * SLME (Syntactic Locality Module Extractor) extraction methods.
 *
 * <p>
 * SLME methods preserve logical entailments between seed terms and their signatures. Each
 * method takes a different view of the class hierarchy, producing modules of varying sizes
 * and coverage.
 *
 * @see <a href="https://robot.obolibrary.org/extract#slme">ROBOT SLME Documentation</a>
 */
public enum SlmeExtractMethod {

  /**
   * Star module: minimal extraction with seed terms and inter-relations only.
   *
   * <p>
   * Produces the smallest modules by excluding unnecessary class hierarchies. Star modules
   * contain only the seed terms and their direct relationships, without including superclasses
   * or subclasses unless they are also seed terms.
   *
   * <p>
   * Use when you need the absolute minimum set of terms and relationships.
   */
  STAR,

  /**
   * Top module: includes seed terms, all subclasses, and inter-relations.
   *
   * <p>
   * Takes a view from the top of the class hierarchy downwards, including all descendants of
   * the seed terms. This typically produces large modules.
   *
   * <p>
   * Use only when all descendants of the seed terms are necessary for your use case.
   */
  TOP,

  /**
   * Bottom module: includes seed terms, all superclasses, and inter-relations.
   *
   * <p>
   * Takes a view from the bottom of the class hierarchy upwards, including all ancestors of
   * the seed terms. This produces medium-sized modules and is the recommended default when
   * uncertain which method to use.
   *
   * <p>
   * This is the most commonly used SLME method as it provides good context by showing how
   * seed terms fit into the broader ontology structure.
   */
  BOT;
}
