package edu.stanford.protege.robot.command.materialize;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import edu.stanford.protege.robot.command.RobotCommand;
import edu.stanford.protege.robot.command.common.Reasoner;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import org.obolibrary.robot.Command;
import org.obolibrary.robot.MaterializeCommand;

/**
 * ROBOT materialize command for materializing inferred superclass expressions.
 *
 * <p>
 * The materialize command uses the Expression Materializing Reasoner (EMR) to assert inferred
 * parents of the form {@code P some D} for specified properties. It wraps an OWL reasoner (ELK or
 * HermiT) to compute the materialized class hierarchy.
 *
 * <p>
 * This is useful for making implicit knowledge explicit in ontologies, particularly for properties
 * like {@code part_of} or {@code has_part} where existential restrictions should be asserted as
 * named superclasses.
 *
 * @param reasoner
 *            the OWL reasoner to use for materialization (optional, defaults to ROBOT's default if
 *            null). Supported reasoners: {@link Reasoner#ELK} and {@link Reasoner#HERMIT}.
 * @param terms
 *            list of property term CURIEs or full IRIs to materialize (empty list means materialize
 *            all properties)
 * @param flags
 *            varargs array of materialize flags to enable specific operations. Supported flags:
 *            {@link MaterializeFlags#REMOVE_REDUNDANT_SUBCLASS_AXIOMS},
 *            {@link MaterializeFlags#CREATE_NEW_ONTOLOGY}, and
 *            {@link MaterializeFlags#ANNOTATE_INFERRED_AXIOMS}.
 *
 * @see <a href="https://robot.obolibrary.org/materialize">ROBOT Materialize Documentation</a>
 */
@JsonTypeName("MaterializeCommand")
public record RobotMaterializeCommand(
        @Nullable Reasoner reasoner, List<String> terms, MaterializeFlags... flags)
        implements
            RobotCommand {

    /**
     * Converts this materialize command to ROBOT command-line arguments.
     *
     * <p>
     * Generates arguments in the format:
     * {@code [--reasoner NAME] [--term TERM]... [--flag true]...}
     *
     * @return immutable list of command-line arguments for ROBOT materialize
     */
    @Override
    public List<String> getArgs() {
        var args = ImmutableList.<String>builder();

        // Add reasoner if specified
        if (reasoner != null) {
            args.add("--reasoner");
            args.add(reasoner.getReasonerName());
        }

        // Add terms (repeated --term flag)
        terms.forEach(
                term -> {
                    args.add("--term");
                    args.add(term);
                });

        // Process flags using Arrays.asList() for varargs
        List<MaterializeFlags> flagsList = Arrays.asList(flags);

        // Add remove-redundant-subclass-axioms flag if present
        if (flagsList.contains(MaterializeFlags.REMOVE_REDUNDANT_SUBCLASS_AXIOMS)) {
            args.add(MaterializeFlags.REMOVE_REDUNDANT_SUBCLASS_AXIOMS.getFlagName());
            args.add("true");
        }

        // Add create-new-ontology flag if present
        if (flagsList.contains(MaterializeFlags.CREATE_NEW_ONTOLOGY)) {
            args.add(MaterializeFlags.CREATE_NEW_ONTOLOGY.getFlagName());
            args.add("true");
        }

        // Add annotate-inferred-axioms flag if present
        if (flagsList.contains(MaterializeFlags.ANNOTATE_INFERRED_AXIOMS)) {
            args.add(MaterializeFlags.ANNOTATE_INFERRED_AXIOMS.getFlagName());
            args.add("true");
        }

        return args.build();
    }

    /**
     * Returns the ROBOT MaterializeCommand instance for execution.
     *
     * @return a new MaterializeCommand instance
     */
    @Override
    public Command getCommand() {
        return new MaterializeCommand();
    }
}
