package edu.stanford.protege.robot.command.export;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import edu.stanford.protege.robot.command.RobotCommand;
import java.util.List;
import javax.annotation.Nullable;
import org.obolibrary.robot.Command;
import org.obolibrary.robot.ExportCommand;

/**
 * ROBOT export command for generating tabular representations of ontology entities.
 *
 * <p>
 * The export command transforms ontology data into structured formats suitable for analysis and
 * documentation. It generates tables with configurable columns, sorting, and entity filtering.
 *
 * <p>
 * Supported export formats:
 *
 * <ul>
 * <li>{@link ExportFormat#tsv} - Tab-separated values (default for unknown extensions)</li>
 * <li>{@link ExportFormat#csv} - Comma-separated values</li>
 * <li>{@link ExportFormat#html} - Bootstrap-styled HTML tables</li>
 * <li>{@link ExportFormat#html_list} - Bootstrap-styled bullet lists</li>
 * <li>{@link ExportFormat#json} - JSON with array values</li>
 * <li>{@link ExportFormat#xlsx} - Excel workbooks</li>
 * </ul>
 *
 * @param header
 *            pipe-separated list of column definitions (required). Special keywords include: IRI,
 *            ID,
 *            LABEL, SYNONYMS, SubClass Of, SubClasses, Equivalent Class, SubProperty Of, Equivalent
 *            Property, Disjoint With, Type, Domain, Range. May also reference properties by CURIE
 *            or
 *            label.
 * @param format
 *            output format (optional, can be null). When null, format is auto-detected from file
 *            extension. Supported formats: tsv, csv, html, html-list, json, xlsx.
 * @param sort
 *            list of column(s) to sort by (empty list for default sorting by first column). Prefix
 *            column name with ^ for reverse order (e.g., "^LABEL").
 * @param delimiter
 *            custom delimiter for multi-value cells (optional, can be null). Default is "|" when
 *            null.
 * @param include
 *            entity types to export (empty list includes all). Valid values: "classes",
 *            "individuals", "properties".
 * @param entitySelect
 *            filter entities by anonymity (optional, can be null). Options: ANY (all entities,
 *            default), NAMED (only named entities), ANON/ANONYMOUS (only anonymous entities).
 * @param entityFormat
 *            rendering strategy for entities (optional, can be null). Options: NAME (local name),
 *            ID (CURIE), IRI (full IRI), LABEL (rdfs:label).
 *
 * @see <a href="https://robot.obolibrary.org/export">ROBOT Export Documentation</a>
 */
@JsonTypeName("ExportCommand")
public record RobotExportCommand(
        String header,
        @Nullable ExportFormat format,
        List<String> sort,
        @Nullable String delimiter,
        List<String> include,
        @Nullable EntitySelect entitySelect,
        @Nullable EntityFormat entityFormat)
        implements
            RobotCommand {

    /**
     * Converts this export command to ROBOT command-line arguments.
     *
     * <p>
     * Generates arguments in the format:
     * {@code --header "COLUMNS" [--format FORMAT] [--sort COLUMN]... [--split DELIMITER]
     * [--include TYPE]... [--entity-select FILTER] [--entity-format RENDERING]}
     *
     * @return immutable list of command-line arguments for ROBOT export
     */
    @Override
    public List<String> getArgs() {
        var args = ImmutableList.<String>builder();

        // Add header (required)
        args.add("--header");
        args.add(header);

        // Add format if specified
        if (format != null) {
            args.add("--format");
            args.add(format.name());
        }

        // Add sort columns (repeated --sort flag)
        sort.forEach(
                column -> {
                    args.add("--sort");
                    args.add(column);
                });

        // Add delimiter delimiter if specified
        if (delimiter != null) {
            args.add("--split");
            args.add(delimiter);
        }

        // Add include entity types (repeated --include flag)
        include.forEach(
                entityType -> {
                    args.add("--include");
                    args.add(entityType);
                });

        // Add entity-select if specified
        if (entitySelect != null) {
            args.add("--entity-select");
            args.add(entitySelect.name());
        }

        // Add entity-format if specified
        if (entityFormat != null) {
            args.add("--entity-format");
            args.add(entityFormat.name());
        }

        return args.build();
    }

    /**
     * Returns the ROBOT ExportCommand instance for execution.
     *
     * @return a new ExportCommand instance
     */
    @Override
    public Command getCommand() {
        return new ExportCommand();
    }
}
