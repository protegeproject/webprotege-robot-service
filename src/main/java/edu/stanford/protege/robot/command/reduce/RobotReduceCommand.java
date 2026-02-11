package edu.stanford.protege.robot.command.reduce;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import edu.stanford.protege.robot.command.RobotCommand;
import edu.stanford.protege.robot.command.common.Reasoner;
import java.util.Arrays;
import java.util.List;
import org.obolibrary.robot.Command;
import org.obolibrary.robot.ReduceCommand;

/**
 * ROBOT reduce command for removing redundant subClassOf axioms using automated reasoning.
 *
 * <p>
 * The reduce command uses a reasoner to identify and remove subClassOf axioms that are already
 * entailed by other axioms in the ontology. This simplifies the class hierarchy by eliminating
 * redundant relationships without changing the logical content.
 *
 * @param reasoner
 *            the OWL reasoner to use for entailment checking. Required parameter that determines
 *            which reasoning engine evaluates axiom redundancy. Available reasoners: {@link
 *            Reasoner#ELK}, {@link Reasoner#HERMIT}, {@link Reasoner#JFACT},
 *            {@link Reasoner#WHELK}, {@link Reasoner#STRUCTURAL}.
 * @param flags
 *            optional boolean flags for non-default reduction behaviors. Available flags: {@link
 *            ReduceFlags#PRESERVE_ANNOTATED_AXIOMS}, {@link ReduceFlags#NAMED_CLASSES_ONLY},
 *            {@link ReduceFlags#INCLUDE_SUBPROPERTIES}. If not specified, ROBOT defaults are used.
 * 
 * @see <a href="https://robot.obolibrary.org/reduce">ROBOT Reduce Documentation</a>
 */
@JsonTypeName("ReduceCommand")
public record RobotReduceCommand(Reasoner reasoner, ReduceFlags... flags)
        implements
            RobotCommand {

    /**
     * Converts this reduce command to ROBOT command-line arguments.
     *
     * <p>
     * Always emits {@code --reasoner <name>} first, followed by any specified flags with value
     * {@code "true"}.
     *
     * @return immutable list of command-line arguments for ROBOT reduce
     */
    @Override
    public List<String> getArgs() {
        var args = ImmutableList.<String>builder();

        // Always add required reasoner argument
        args.add("--reasoner");
        args.add(reasoner.getReasonerName());

        // Add flags (each flag enables a non-default behavior)
        List<ReduceFlags> flagsList = Arrays.asList(flags);
        if (flagsList.contains(ReduceFlags.PRESERVE_ANNOTATED_AXIOMS)) {
            args.add(ReduceFlags.PRESERVE_ANNOTATED_AXIOMS.getFlagName());
            args.add("true");
        }
        if (flagsList.contains(ReduceFlags.NAMED_CLASSES_ONLY)) {
            args.add(ReduceFlags.NAMED_CLASSES_ONLY.getFlagName());
            args.add("true");
        }
        if (flagsList.contains(ReduceFlags.INCLUDE_SUBPROPERTIES)) {
            args.add(ReduceFlags.INCLUDE_SUBPROPERTIES.getFlagName());
            args.add("true");
        }

        return args.build();
    }

    /**
     * Returns the ROBOT ReduceCommand instance for execution.
     *
     * @return a new ReduceCommand instance
     */
    @Override
    public Command getCommand() {
        return new ReduceCommand();
    }
}
