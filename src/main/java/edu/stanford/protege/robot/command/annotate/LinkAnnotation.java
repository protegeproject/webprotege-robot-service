package edu.stanford.protege.robot.command.annotate;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * Annotation for IRI references.
 *
 * <p>
 * Maps to ROBOT's {@code --link-annotation} flag for values that are IRIs (not literals).
 *
 * <p>
 * Example: {@code new LinkAnnotation("dc:license", "http://creativecommons.org/licenses/by/4.0/")}
 * generates {@code --link-annotation dc:license http://creativecommons.org/licenses/by/4.0/}
 *
 * @param property
 *          the annotation property IRI or prefixed name
 * @param value
 *          the IRI value
 */
@JsonTypeName("LinkAnnotation")
public record LinkAnnotation(String property, String value) implements Annotation {

  public static final String LINK_ANNOTATION = "--link-annotation";

  @Override
  public String getArgName() {
    return LINK_ANNOTATION;
  }

  @Override
  public List<String> getArgs() {
    return ImmutableList.of(getArgName(), property, value);
  }
}
