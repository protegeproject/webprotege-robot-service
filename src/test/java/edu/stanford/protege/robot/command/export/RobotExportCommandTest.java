package edu.stanford.protege.robot.command.export;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.obolibrary.robot.ExportCommand;

class RobotExportCommandTest {

    @Nested
    class GetCommand {

        @Test
        void shouldReturnExportCommandInstance() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of(), null, null);

            var result = command.getCommand();

            assertThat(result).isNotNull().isInstanceOf(ExportCommand.class);
        }
    }

    @Nested
    class GetArgsWithMinimalConfiguration {

        @Test
        void shouldIncludeOnlyHeader() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL");
        }

        @Test
        void shouldHandleComplexHeaderWithSpaces() {
            var command = new RobotExportCommand(
                    "ID|LABEL|SubClass Of", null, List.of(), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL|SubClass Of");
        }

        @Test
        void shouldHandleHeaderWithMultipleColumns() {
            var command = new RobotExportCommand(
                    "IRI|ID|LABEL|SYNONYMS|SubClass Of|Equivalent Class",
                    null,
                    List.of(),
                    null,
                    List.of(),
                    null,
                    null);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly("--header", "IRI|ID|LABEL|SYNONYMS|SubClass Of|Equivalent Class");
        }
    }

    @Nested
    class GetArgsWithFormat {

        @Test
        void shouldIncludeTsvFormat() {
            var command = new RobotExportCommand(
                    "ID|LABEL", ExportFormat.tsv, List.of(), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL", "--format", "tsv");
        }

        @Test
        void shouldIncludeCsvFormat() {
            var command = new RobotExportCommand(
                    "ID|LABEL", ExportFormat.csv, List.of(), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL", "--format", "csv");
        }

        @Test
        void shouldIncludeHtmlFormat() {
            var command = new RobotExportCommand(
                    "ID|LABEL", ExportFormat.html, List.of(), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL", "--format", "html");
        }

        @Test
        void shouldIncludeHtmlListFormat() {
            var command = new RobotExportCommand(
                    "ID|LABEL", ExportFormat.html_list, List.of(), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL", "--format", "html_list");
        }

        @Test
        void shouldIncludeJsonFormat() {
            var command = new RobotExportCommand(
                    "ID|LABEL", ExportFormat.json, List.of(), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL", "--format", "json");
        }

        @Test
        void shouldIncludeXlsxFormat() {
            var command = new RobotExportCommand(
                    "ID|LABEL", ExportFormat.xlsx, List.of(), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL", "--format", "xlsx");
        }

        @Test
        void shouldOmitFormatWhenNull() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args).doesNotContain("--format");
        }
    }

    @Nested
    class GetArgsWithSort {

        @Test
        void shouldNotIncludeSortWhenListIsEmpty() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args).doesNotContain("--sort");
        }

        @Test
        void shouldIncludeSingleSortColumn() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of("LABEL"), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL", "--sort", "LABEL");
        }

        @Test
        void shouldIncludeMultipleSortColumns() {
            var command = new RobotExportCommand(
                    "ID|LABEL|SubClass Of", null, List.of("LABEL", "ID"), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly("--header", "ID|LABEL|SubClass Of", "--sort", "LABEL", "--sort", "ID");
        }

        @Test
        void shouldHandleReverseSortWithCaretPrefix() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of("^LABEL"), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL", "--sort", "^LABEL");
        }

        @Test
        void shouldHandleMixedSortDirections() {
            var command = new RobotExportCommand(
                    "ID|LABEL|SubClass Of", null, List.of("LABEL", "^ID"), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--header", "ID|LABEL|SubClass Of", "--sort", "LABEL", "--sort", "^ID");
        }
    }

    @Nested
    class GetArgsWithSplit {

        @Test
        void shouldOmitSplitWhenNull() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args).doesNotContain("--split");
        }

        @Test
        void shouldIncludeCustomSplitDelimiter() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), ",", List.of(), null, null);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL", "--split", ",");
        }

        @Test
        void shouldHandleSemicolonDelimiter() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), ";", List.of(), null, null);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL", "--split", ";");
        }
    }

    @Nested
    class GetArgsWithInclude {

        @Test
        void shouldNotIncludeWhenListIsEmpty() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args).doesNotContain("--include");
        }

        @Test
        void shouldIncludeSingleEntityType() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of("classes"), null, null);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL", "--include", "classes");
        }

        @Test
        void shouldIncludeMultipleEntityTypes() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of("classes", "properties"), null, null);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--header", "ID|LABEL", "--include", "classes", "--include", "properties");
        }

        @Test
        void shouldIncludeAllThreeEntityTypes() {
            var command = new RobotExportCommand(
                    "ID|LABEL",
                    null,
                    List.of(),
                    null,
                    List.of("classes", "individuals", "properties"),
                    null,
                    null);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--header",
                            "ID|LABEL",
                            "--include",
                            "classes",
                            "--include",
                            "individuals",
                            "--include",
                            "properties");
        }
    }

    @Nested
    class GetArgsWithEntitySelect {

        @Test
        void shouldOmitEntitySelectWhenNull() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args).doesNotContain("--entity-select");
        }

        @Test
        void shouldIncludeEntitySelectAny() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of(), EntitySelect.ANY, null);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL", "--entity-select", "ANY");
        }

        @Test
        void shouldIncludeEntitySelectNamed() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of(), EntitySelect.NAMED, null);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL", "--entity-select", "NAMED");
        }

        @Test
        void shouldIncludeEntitySelectAnon() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of(), EntitySelect.ANON, null);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL", "--entity-select", "ANON");
        }

        @Test
        void shouldIncludeEntitySelectAnonymous() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of(), EntitySelect.ANONYMOUS, null);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL", "--entity-select", "ANONYMOUS");
        }
    }

    @Nested
    class GetArgsWithEntityFormat {

        @Test
        void shouldOmitEntityFormatWhenNull() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args).doesNotContain("--entity-format");
        }

        @Test
        void shouldIncludeEntityFormatName() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of(), null, EntityFormat.NAME);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL", "--entity-format", "NAME");
        }

        @Test
        void shouldIncludeEntityFormatId() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of(), null, EntityFormat.ID);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL", "--entity-format", "ID");
        }

        @Test
        void shouldIncludeEntityFormatIri() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of(), null, EntityFormat.IRI);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL", "--entity-format", "IRI");
        }

        @Test
        void shouldIncludeEntityFormatLabel() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of(), null, EntityFormat.LABEL);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL", "--entity-format", "LABEL");
        }
    }

    @Nested
    class GetArgsWithCombinedParameters {

        @Test
        void shouldCombineFormatAndSort() {
            var command = new RobotExportCommand(
                    "ID|LABEL", ExportFormat.csv, List.of("LABEL"), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly("--header", "ID|LABEL", "--format", "csv", "--sort", "LABEL");
        }

        @Test
        void shouldCombineFormatSortAndInclude() {
            var command = new RobotExportCommand(
                    "ID|LABEL|SubClass Of",
                    ExportFormat.html,
                    List.of("LABEL"),
                    null,
                    List.of("classes"),
                    null,
                    null);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--header",
                            "ID|LABEL|SubClass Of",
                            "--format",
                            "html",
                            "--sort",
                            "LABEL",
                            "--include",
                            "classes");
        }

        @Test
        void shouldCombineAllParameters() {
            var command = new RobotExportCommand(
                    "ID|LABEL|SubClass Of",
                    ExportFormat.csv,
                    List.of("LABEL", "ID"),
                    "|",
                    List.of("classes", "properties"),
                    EntitySelect.NAMED,
                    EntityFormat.LABEL);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--header",
                            "ID|LABEL|SubClass Of",
                            "--format",
                            "csv",
                            "--sort",
                            "LABEL",
                            "--sort",
                            "ID",
                            "--split",
                            "|",
                            "--include",
                            "classes",
                            "--include",
                            "properties",
                            "--entity-select",
                            "NAMED",
                            "--entity-format",
                            "LABEL");
        }

        @Test
        void shouldHandleComplexExportConfiguration() {
            var command = new RobotExportCommand(
                    "IRI|ID|LABEL|SYNONYMS|SubClass Of|Equivalent Class|Disjoint With",
                    ExportFormat.xlsx,
                    List.of("^LABEL", "ID"),
                    ";",
                    List.of("classes", "individuals", "properties"),
                    EntitySelect.NAMED,
                    EntityFormat.ID);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--header",
                            "IRI|ID|LABEL|SYNONYMS|SubClass Of|Equivalent Class|Disjoint With",
                            "--format",
                            "xlsx",
                            "--sort",
                            "^LABEL",
                            "--sort",
                            "ID",
                            "--split",
                            ";",
                            "--include",
                            "classes",
                            "--include",
                            "individuals",
                            "--include",
                            "properties",
                            "--entity-select",
                            "NAMED",
                            "--entity-format",
                            "ID");
        }
    }

    @Nested
    class GetArgsArray {

        @Test
        void shouldConvertToArrayWithMinimalArgs() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of(), null, null);

            var argsArray = command.getArgsArray();

            assertThat(argsArray).containsExactly("--header", "ID|LABEL");
        }

        @Test
        void shouldConvertToArrayWithAllParameters() {
            var command = new RobotExportCommand(
                    "ID|LABEL",
                    ExportFormat.csv,
                    List.of("LABEL"),
                    "|",
                    List.of("classes"),
                    EntitySelect.NAMED,
                    EntityFormat.LABEL);

            var argsArray = command.getArgsArray();

            assertThat(argsArray)
                    .containsExactly(
                            "--header",
                            "ID|LABEL",
                            "--format",
                            "csv",
                            "--sort",
                            "LABEL",
                            "--split",
                            "|",
                            "--include",
                            "classes",
                            "--entity-select",
                            "NAMED",
                            "--entity-format",
                            "LABEL");
        }
    }

    @Nested
    class Immutability {

        @Test
        void shouldReturnImmutableList() {
            var command = new RobotExportCommand(
                    "ID|LABEL",
                    ExportFormat.csv,
                    List.of("LABEL"),
                    null,
                    List.of("classes"),
                    null,
                    null);

            var args = command.getArgs();

            assertThat(args).isUnmodifiable();
        }

        @Test
        void shouldReturnImmutableListWithMinimalArgs() {
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args).isUnmodifiable();
        }
    }

    @Nested
    class RealWorldScenarios {

        @Test
        void shouldHandleBasicClassExport() {
            // Basic export of class IDs and labels
            var command = new RobotExportCommand(
                    "ID|LABEL", null, List.of(), null, List.of(), null, null);

            var args = command.getArgs();

            assertThat(args).containsExactly("--header", "ID|LABEL");
        }

        @Test
        void shouldHandleHierarchyExport() {
            // Export class hierarchy with subclass relationships
            var command = new RobotExportCommand(
                    "ID|LABEL|SubClass Of",
                    ExportFormat.csv,
                    List.of("LABEL"),
                    null,
                    List.of("classes"),
                    EntitySelect.NAMED,
                    null);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--header",
                            "ID|LABEL|SubClass Of",
                            "--format",
                            "csv",
                            "--sort",
                            "LABEL",
                            "--include",
                            "classes",
                            "--entity-select",
                            "NAMED");
        }

        @Test
        void shouldHandleCompleteOntologyExport() {
            // Comprehensive export for documentation
            var command = new RobotExportCommand(
                    "IRI|ID|LABEL|SYNONYMS|SubClass Of|Equivalent Class",
                    ExportFormat.html,
                    List.of("LABEL"),
                    null,
                    List.of(),
                    EntitySelect.NAMED,
                    EntityFormat.LABEL);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--header",
                            "IRI|ID|LABEL|SYNONYMS|SubClass Of|Equivalent Class",
                            "--format",
                            "html",
                            "--sort",
                            "LABEL",
                            "--entity-select",
                            "NAMED",
                            "--entity-format",
                            "LABEL");
        }

        @Test
        void shouldHandlePropertyExport() {
            // Export properties with domains and ranges
            var command = new RobotExportCommand(
                    "ID|LABEL|Domain|Range",
                    ExportFormat.xlsx,
                    List.of("ID"),
                    null,
                    List.of("properties"),
                    EntitySelect.NAMED,
                    EntityFormat.ID);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--header",
                            "ID|LABEL|Domain|Range",
                            "--format",
                            "xlsx",
                            "--sort",
                            "ID",
                            "--include",
                            "properties",
                            "--entity-select",
                            "NAMED",
                            "--entity-format",
                            "ID");
        }

        @Test
        void shouldHandleJsonExportForProgrammaticUse() {
            // JSON export with full IRIs for API consumption
            var command = new RobotExportCommand(
                    "IRI|LABEL|SubClass Of",
                    ExportFormat.json,
                    List.of(),
                    null,
                    List.of("classes"),
                    EntitySelect.NAMED,
                    EntityFormat.IRI);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--header",
                            "IRI|LABEL|SubClass Of",
                            "--format",
                            "json",
                            "--include",
                            "classes",
                            "--entity-select",
                            "NAMED",
                            "--entity-format",
                            "IRI");
        }
    }
}
