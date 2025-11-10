package edu.stanford.protege.robot.command.repair;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import edu.stanford.protege.robot.command.RobotCommand;
import java.util.Arrays;
import java.util.List;
import org.obolibrary.robot.Command;
import org.obolibrary.robot.RepairCommand;

/**
 * ROBOT repair command for fixing ontology problems.
 *
 * <p>
 * The repair command addresses common ontology issues automatically, including:
 *
 * <ul>
 * <li><b>Invalid references</b> - Updates axioms that reference deprecated classes with their
 * designated replacements (using "term replaced by" property)</li>
 * <li><b>Axiom annotation merging</b> - Consolidates duplicate statements with different axiom
 * annotations into single assertions with combined annotations</li>
 * </ul>
 *
 * <p>
 * When no specific repair operations are specified, all available repairs are executed.
 *
 * @param annotationProperties
 *          list of annotation property CURIEs or full IRIs to migrate when fixing invalid
 *          references (empty list means no annotation migration). By default, annotation axioms
 *          are not migrated.
 * @param flags
 *          varargs array of repair flags to enable specific operations. Supported flags:
 *          {@link RepairFlags#INVALID_REFERENCES} (fix deprecated class references) and
 *          {@link RepairFlags#MERGE_AXIOM_ANNOTATIONS} (merge duplicate axiom annotations).
 *
 * @see <a href="https://robot.obolibrary.org/repair">ROBOT Repair Documentation</a>
 */
@JsonTypeName("RepairCommand")
public record RobotRepairCommand(List<String> annotationProperties, RepairFlags... flags)
    implements
      RobotCommand {

  /**
   * Converts this repair command to ROBOT command-line arguments.
   *
   * @return immutable list of command-line arguments for ROBOT repair
   */
  @Override
  public List<String> getArgs() {
    var args = ImmutableList.<String>builder();

    // Process flags using Arrays.asList() for varargs
    List<RepairFlags> flagsList = Arrays.asList(flags);

    // Add annotation properties (repeated --annotation-property flag)
    annotationProperties.forEach(
        property -> {
          args.add("--annotation-property");
          args.add(property);
        });

    // Add invalid-references flag if present
    if (flagsList.contains(RepairFlags.INVALID_REFERENCES)) {
      args.add(RepairFlags.INVALID_REFERENCES.getFlagName());
      args.add("true");
    }
    // Add merge-axiom-annotations flag if present
    if (flagsList.contains(RepairFlags.MERGE_AXIOM_ANNOTATIONS)) {
      args.add(RepairFlags.MERGE_AXIOM_ANNOTATIONS.getFlagName());
      args.add("true");
    }

    return args.build();
  }

  /**
   * Returns the ROBOT RepairCommand instance for execution.
   *
   * @return a new RepairCommand instance
   */
  @Override
  public Command getCommand() {
    return new RepairCommand();
  }
}
