package edu.stanford.protege.robot.command.annotate;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * Simple annotation with property and value.
 *
 * <p>
 * Maps to ROBOT's {@code --annotation} flag for basic property-value pairs.
 *
 * <p>
 * Example: {@code new PlainAnnotation("rdfs:comment", "This is a comment")} generates {@code
 * --annotation rdfs:comment "This is a comment"}
 *
 * @param property
 *          the annotation property IRI or prefixed name
 * @param value
 *          the annotation value
 */
@JsonTypeName("PlainAnnotation")
public record PlainAnnotation(String property, String value) implements Annotation {

  public static final String ANNOTATION = "--annotation";

  @Override
  public String getArgName() {
    return ANNOTATION;
  }

  @Override
  public List<String> getArgs() {
    return ImmutableList.of(getArgName(), property, value);
  }
}
