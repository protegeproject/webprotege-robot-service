package edu.stanford.protege.robot.command.convert;

import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.robot.command.RobotCommand;
import java.util.List;
import org.obolibrary.robot.Command;
import org.obolibrary.robot.ConvertCommand;

/**
 * ROBOT convert command for transforming ontologies between different formats.
 *
 * <p>
 * The convert command enables format transformation between multiple OWL, RDF, and OBO formats
 * using a convertStrategy pattern. Each format has its own convertStrategy implementation that
 * encapsulates
 * format-specific parameters and options.
 *
 * <p>
 * Supported formats include:
 *
 * <ul>
 * <li>{@link JsonConvertStrategy} - JSON (OBO Graphs)</li>
 * <li>{@link OboConvertStrategy} - OBO (legacy text) with validation and cleaning options</li>
 * <li>{@link OfnConvertStrategy} - OWL Functional Syntax</li>
 * <li>{@link OmnConvertStrategy} - Manchester Syntax</li>
 * <li>{@link OwlConvertStrategy} - RDF/XML (default ROBOT format)</li>
 * <li>{@link OwxConvertStrategy} - OWL/XML</li>
 * <li>{@link TtlConvertStrategy} - Turtle</li>
 * </ul>
 *
 * @param convertStrategy
 *            the conversion convertStrategy specifying the target format and format-specific
 *            options
 *
 * @see <a href="https://robot.obolibrary.org/convert">ROBOT Convert Documentation</a>
 */
@JsonTypeName("ConvertCommand")
public record RobotConvertCommand(ConvertStrategy convertStrategy) implements RobotCommand {

    /**
     * Converts this convert command to ROBOT command-line arguments.
     *
     * <p>
     * Delegates argument generation to the configured convertStrategy, which produces format-specific
     * flags
     * and options.
     *
     * @return immutable list of command-line arguments for ROBOT convert
     */
    @Override
    public List<String> getArgs() {
        return convertStrategy.getArgs();
    }

    /**
     * Returns the ROBOT ConvertCommand instance for execution.
     *
     * @return a new ConvertCommand instance
     */
    @Override
    public Command getCommand() {
        return new ConvertCommand();
    }
}
