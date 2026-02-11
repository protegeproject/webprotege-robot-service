package edu.stanford.protege.robot.command;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.stanford.protege.robot.command.annotate.RobotAnnotateCommand;
import edu.stanford.protege.robot.command.collapse.RobotCollapseCommand;
import edu.stanford.protege.robot.command.convert.RobotConvertCommand;
import edu.stanford.protege.robot.command.expand.RobotExpandCommand;
import edu.stanford.protege.robot.command.export.RobotExportCommand;
import edu.stanford.protege.robot.command.extract.RobotExtractCommand;
import edu.stanford.protege.robot.command.filter.RobotFilterCommand;
import edu.stanford.protege.robot.command.merge.RobotMergeCommand;
import edu.stanford.protege.robot.command.reduce.RobotReduceCommand;
import edu.stanford.protege.robot.command.relax.RobotRelaxCommand;
import edu.stanford.protege.robot.command.remove.RobotRemoveCommand;
import edu.stanford.protege.robot.command.repair.RobotRepairCommand;
import java.util.List;
import org.obolibrary.robot.Command;

/**
 * Core interface for all ROBOT command wrappers.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({
        @JsonSubTypes.Type(RobotAnnotateCommand.class),
        @JsonSubTypes.Type(RobotExtractCommand.class),
        @JsonSubTypes.Type(RobotCollapseCommand.class),
        @JsonSubTypes.Type(RobotConvertCommand.class),
        @JsonSubTypes.Type(RobotExpandCommand.class),
        @JsonSubTypes.Type(RobotExportCommand.class),
        @JsonSubTypes.Type(RobotFilterCommand.class),
        @JsonSubTypes.Type(RobotMergeCommand.class),
        @JsonSubTypes.Type(RobotReduceCommand.class),
        @JsonSubTypes.Type(RobotRelaxCommand.class),
        @JsonSubTypes.Type(RobotRemoveCommand.class),
        @JsonSubTypes.Type(RobotRepairCommand.class)})
public interface RobotCommand {

    @JsonIgnore
    List<String> getArgs();

    @JsonIgnore
    Command getCommand();

    @JsonIgnore
    default String[] getArgsArray() {
        return getArgs().toArray(new String[getArgs().size()]);
    }
}
