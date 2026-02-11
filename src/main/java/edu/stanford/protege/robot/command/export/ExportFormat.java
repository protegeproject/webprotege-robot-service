package edu.stanford.protege.robot.command.export;

/**
 * Supported export formats for ROBOT export command.
 *
 * <p>
 * The export command can generate tabular representations in multiple formats. Format is
 * automatically detected from file extension when not explicitly specified.
 */
public enum ExportFormat {

    /**
     * Tab-separated values format (TSV). Default format for unknown file extensions. Each row
     * represents an entity with column values
     * separated by tabs.
     */
    tsv,

    /**
     * Comma-separated values format (CSV). Standard CSV format with comma-delimited columns. Suitable
     * for spreadsheet applications.
     */
    csv,

    /**
     * HTML table format with Bootstrap styling. Generates a Bootstrap-styled HTML table with sortable
     * columns.
     */
    html,

    /**
     * HTML bullet list format with Bootstrap styling. Generates a Bootstrap-styled HTML page with
     * bullet-point lists for entity details.
     */
    html_list,

    /**
     * JSON format with array values. Exports data as JSON with string IDs or IRIs. Suitable for
     * programmatic processing.
     */
    json,

    /**
     * Microsoft Excel workbook format (XLSX). Creates an Excel-compatible spreadsheet with formatted
     * columns.
     */
    xlsx,
    ;
}
