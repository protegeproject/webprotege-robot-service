package edu.stanford.protege.robot.command;

import java.util.List;

import org.obolibrary.robot.Command;

public interface RobotCommand {

  List<String> getArgs();

  Command getCommand();

  default String[] getArgsArray() {
    return getArgs().toArray(new String[getArgs().size()]);
  }
}
