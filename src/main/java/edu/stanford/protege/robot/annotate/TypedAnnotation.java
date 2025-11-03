package edu.stanford.protege.robot.annotate;

import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * Annotation with explicit datatype.
 *
 * <p>
 * Maps to ROBOT's {@code --typed-annotation} flag for values with specific datatypes.
 *
 * <p>
 * Example: {@code new TypedAnnotation("ex:count", "42", "xsd:integer")} generates {@code
 * --typed-annotation ex:count 42 xsd:integer}
 *
 * @param property
 *          the annotation property IRI or prefixed name
 * @param value
 *          the annotation value
 * @param type
 *          the XSD datatype IRI or prefixed name
 */
public record TypedAnnotation(String property, String value, String type) implements Annotation, HasType {

  public static final String ANNOTATION = "--typed-annotation";

  @Override
  public String getArgName() {
    return ANNOTATION;
  }

  @Override
  public List<String> getArgs() {
    return ImmutableList.of(getArgName(), property, value, type);
  }
}
