package edu.stanford.protege.robot.command.extract;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;

/**
 * Strategy interface for different ontology extraction methods in ROBOT.
 *
 * <p>
 * Extraction enables reuse of ontology term subsets rather than entire ontologies,
 * creating focused modules containing selected terms and their supporting relationships.
 *
 * <p>
 * Implementations include:
 * <ul>
 * <li>{@link SlmeExtractStrategy} - Syntactic Locality Module Extractor (SLME) that preserves
 * logical entailments</li>
 * <li>{@link MireotExtractStrategy} - MIREOT method that preserves hierarchy structure</li>
 * <li>{@link SubsetExtractStrategy} - Subset method using relation-graph to materialize
 * existential relations</li>
 * </ul>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({
    @JsonSubTypes.Type(SlmeExtractStrategy.class),
    @JsonSubTypes.Type(MireotExtractStrategy.class),
    @JsonSubTypes.Type(SubsetExtractStrategy.class)})
public interface ExtractStrategy {

  /**
   * Converts this extraction strategy to command-line arguments for ROBOT.
   *
   * @return a list of command-line arguments representing this extraction strategy
   */
  @JsonIgnore
  List<String> getArgs();
}
