package edu.stanford.protege.robot.command.merge;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import edu.stanford.protege.robot.command.RobotCommand;
import edu.stanford.protege.robot.pipeline.RelativePath;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import org.obolibrary.robot.Command;
import org.obolibrary.robot.MergeCommand;

/**
 * ROBOT merge command for consolidating multiple OWL ontologies into one.
 *
 * <p>
 * The merge command combines multiple input ontologies into a single ontology. This is useful for
 * combining modular ontologies, merging imports, and creating unified ontology files. Options
 * control whether import closures are collapsed, annotations are included, and provenance is
 * tracked.
 *
 * <p>
 * The pipeline's main input ontology serves as the base ontology. Additional ontologies to merge
 * can be specified via {@code inputPaths}, which generates {@code --input} arguments for each file.
 *
 * @param inputPaths
 *            additional ontology files to merge into the base ontology. Each path generates an
 *            {@code --input <path>} argument. May be null or empty if only merging the pipeline's
 *            main input ontology with its imports.
 * @param flags
 *            boolean flags for non-default merge behaviors. Available flags: {@link
 *            MergeFlags#NO_COLLAPSE_IMPORT_CLOSURE}, {@link MergeFlags#INCLUDE_ANNOTATIONS}, {@link
 *            MergeFlags#ANNOTATE_DERIVED_FROM}, {@link MergeFlags#ANNOTATE_DEFINED_BY}. If not
 *            specified, ROBOT defaults are used (collapse import closure, no annotation inclusion,
 *            no
 *            provenance tracking).
 *
 * @see <a href="https://robot.obolibrary.org/merge">ROBOT Merge Documentation</a>
 */
@JsonTypeName("MergeCommand")
public record RobotMergeCommand(
        @Nullable List<RelativePath> inputPaths,
        MergeFlags... flags) implements RobotCommand {

    /**
     * Creates a merge command with only flags (no additional input files).
     *
     * @param flags
     *            the merge flags
     */
    public RobotMergeCommand(MergeFlags... flags) {
        this(null, flags);
    }

    /**
     * Converts this merge command to ROBOT command-line arguments.
     *
     * <p>
     * Generates arguments for controlling how ontologies are merged. Additional input files are
     * specified first via {@code --input} arguments, followed by flag arguments. If no inputs or
     * flags are specified, an empty list is returned and ROBOT's default behavior applies.
     *
     * @return immutable list of command-line arguments for ROBOT merge
     */
    @Override
    public List<String> getArgs() {
        var args = ImmutableList.<String>builder();

        // Add additional input files
        if (inputPaths != null) {
            for (var path : inputPaths) {
                args.add("--input");
                args.add(path.asString());
            }
        }

        // Add flags (each flag sets a non-default value)
        List<MergeFlags> flagsList = Arrays.asList(flags);
        if (flagsList.contains(MergeFlags.NO_COLLAPSE_IMPORT_CLOSURE)) {
            args.add(MergeFlags.NO_COLLAPSE_IMPORT_CLOSURE.getFlagName());
            args.add("false");
        }
        if (flagsList.contains(MergeFlags.INCLUDE_ANNOTATIONS)) {
            args.add(MergeFlags.INCLUDE_ANNOTATIONS.getFlagName());
            args.add("true");
        }
        if (flagsList.contains(MergeFlags.ANNOTATE_DERIVED_FROM)) {
            args.add(MergeFlags.ANNOTATE_DERIVED_FROM.getFlagName());
            args.add("true");
        }
        if (flagsList.contains(MergeFlags.ANNOTATE_DEFINED_BY)) {
            args.add(MergeFlags.ANNOTATE_DEFINED_BY.getFlagName());
            args.add("true");
        }

        return args.build();
    }

    /**
     * Returns the ROBOT MergeCommand instance for execution.
     *
     * @return a new MergeCommand instance
     */
    @Override
    public Command getCommand() {
        return new MergeCommand();
    }
}
