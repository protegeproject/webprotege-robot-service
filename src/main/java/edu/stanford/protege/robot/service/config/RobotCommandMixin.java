package edu.stanford.protege.robot.service.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.stanford.protege.robot.annotate.RobotAnnotateCommand;
import edu.stanford.protege.robot.collapse.RobotCollapseCommand;
import edu.stanford.protege.robot.convert.RobotConvertCommand;
import edu.stanford.protege.robot.expand.RobotExpandCommand;
import edu.stanford.protege.robot.extract.RobotExtractCommand;
import java.util.List;
import org.obolibrary.robot.Command;

/**
 * Jackson mix-in for polymorphic deserialization of {@link edu.stanford.protege.RobotCommand}
 * interface.
 *
 * <p>
 * This mix-in enables Jackson to correctly deserialize JSON into the appropriate RobotCommand
 * implementation class based on a type discriminator field. The {@code type} field in the JSON
 * determines which concrete command class to instantiate.
 *
 * <p>
 * This mix-in is registered with the ObjectMapper via {@link JacksonConfiguration}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = RobotAnnotateCommand.class, name = "AnnotateCommand"),
    @JsonSubTypes.Type(value = RobotExtractCommand.class, name = "ExtractCommand"),
    @JsonSubTypes.Type(value = RobotCollapseCommand.class, name = "CollapseCommand"),
    @JsonSubTypes.Type(value = RobotConvertCommand.class, name = "ConvertCommand"),
    @JsonSubTypes.Type(value = RobotExpandCommand.class, name = "ExpandCommand")})
public abstract class RobotCommandMixin {

  @JsonIgnore
  public abstract List<String> getArgs();

  @JsonIgnore
  public abstract String[] getArgsArray();

  @JsonIgnore
  public abstract Command getCommand();
}
