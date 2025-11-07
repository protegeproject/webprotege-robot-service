package edu.stanford.protege.robot.service.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.stanford.protege.robot.command.convert.ConvertStrategy;
import edu.stanford.protege.robot.command.convert.JsonConvertStrategy;
import edu.stanford.protege.robot.command.convert.OboConvertStrategy;
import edu.stanford.protege.robot.command.convert.OfnConvertStrategy;
import edu.stanford.protege.robot.command.convert.OmnConvertStrategy;
import edu.stanford.protege.robot.command.convert.OwlConvertStrategy;
import edu.stanford.protege.robot.command.convert.OwxConvertStrategy;
import edu.stanford.protege.robot.command.convert.TtlConvertStrategy;
import java.util.List;

/**
 * Jackson mix-in for polymorphic deserialization of {@link ConvertStrategy} interface.
 *
 * <p>
 * This mix-in enables Jackson to correctly deserialize JSON into the appropriate ConvertStrategy
 * implementation class based on a type discriminator field. The {@code @type} field in the JSON
 * determines which concrete conversion convertStrategy class to instantiate.
 *
 * <p>
 * Supported convertStrategy types:
 *
 * <ul>
 * <li><b>OBO</b> - OBO format with validation and cleaning options</li>
 * <li><b>JSON</b> - OBO Graphs JSON format</li>
 * <li><b>OFN</b> - OWL Functional Syntax</li>
 * <li><b>OMN</b> - Manchester Syntax</li>
 * <li><b>OWL</b> - RDF/XML format</li>
 * <li><b>OWX</b> - OWL/XML format</li>
 * <li><b>TTL</b> - Turtle format</li>
 * </ul>
 *
 * <p>
 * This mix-in is registered with the ObjectMapper via {@link JacksonConfiguration}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = OboConvertStrategy.class, name = "OBO"),
    @JsonSubTypes.Type(value = JsonConvertStrategy.class, name = "JSON"),
    @JsonSubTypes.Type(value = OfnConvertStrategy.class, name = "OFN"),
    @JsonSubTypes.Type(value = OmnConvertStrategy.class, name = "OMN"),
    @JsonSubTypes.Type(value = OwlConvertStrategy.class, name = "OWL"),
    @JsonSubTypes.Type(value = OwxConvertStrategy.class, name = "OWX"),
    @JsonSubTypes.Type(value = TtlConvertStrategy.class, name = "TTL")})
public abstract class ConvertStrategyMixin {

  @JsonIgnore
  public abstract List<String> getArgs();
}
