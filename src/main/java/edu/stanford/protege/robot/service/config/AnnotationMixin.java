package edu.stanford.protege.robot.service.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.stanford.protege.robot.annotate.LanguageAnnotation;
import edu.stanford.protege.robot.annotate.LinkAnnotation;
import edu.stanford.protege.robot.annotate.PlainAnnotation;
import edu.stanford.protege.robot.annotate.TypedAnnotation;
import java.util.List;

/**
 * Jackson mix-in for polymorphic deserialization of
 * {@link edu.stanford.protege.robot.annotate.Annotation}
 * interface.
 *
 * <p>
 * This mix-in enables Jackson to correctly deserialize JSON into the appropriate Annotation
 * implementation class based on a type discriminator field. The {@code type} field in the JSON
 * determines which concrete annotation class to instantiate.
 *
 * <p>
 * This mix-in is registered with the ObjectMapper via {@link JacksonConfiguration}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = PlainAnnotation.class, name = "PlainAnnotation"),
    @JsonSubTypes.Type(value = TypedAnnotation.class, name = "TypedAnnotation"),
    @JsonSubTypes.Type(value = LanguageAnnotation.class, name = "LanguageAnnotation"),
    @JsonSubTypes.Type(value = LinkAnnotation.class, name = "LinkAnnotation")})
public abstract class AnnotationMixin {

  @JsonIgnore
  public abstract String getArgName();

  @JsonIgnore
  public abstract List<String> getArgs();
}
