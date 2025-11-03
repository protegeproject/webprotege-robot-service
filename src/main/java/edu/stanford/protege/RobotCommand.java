package edu.stanford.protege;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import org.obolibrary.robot.Command;

public interface RobotCommand {

  List<String> getArgs();

  @JsonIgnore
  Command getCommand();

  @JsonIgnore
  default String[] getArgsArray() {
    return getArgs().toArray(new String[getArgs().size()]);
  }
}
