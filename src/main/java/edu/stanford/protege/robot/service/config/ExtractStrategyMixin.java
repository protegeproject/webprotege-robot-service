package edu.stanford.protege.robot.service.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.stanford.protege.robot.command.extract.ExtractStrategy;
import edu.stanford.protege.robot.command.extract.MireotExtractStrategy;
import edu.stanford.protege.robot.command.extract.SlmeExtractStrategy;
import edu.stanford.protege.robot.command.extract.SubsetExtractStrategy;
import java.util.List;

/**
 * Jackson mix-in for polymorphic deserialization of
 * {@link ExtractStrategy}
 * interface.
 *
 * <p>
 * This mix-in enables Jackson to correctly deserialize JSON into the appropriate ExtractStrategy
 * implementation class based on a type discriminator field. The {@code type} field in the JSON
 * determines which concrete extraction strategy class to instantiate.
 *
 * <p>
 * This mix-in is registered with the ObjectMapper via {@link JacksonConfiguration}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = SlmeExtractStrategy.class, name = "SLME"),
    @JsonSubTypes.Type(value = MireotExtractStrategy.class, name = "MIREOT"),
    @JsonSubTypes.Type(value = SubsetExtractStrategy.class, name = "Subset")})
public abstract class ExtractStrategyMixin {

  @JsonIgnore
  public abstract List<String> getArgs();
}
