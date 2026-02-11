package edu.stanford.protege.robot.command.repair;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.obolibrary.robot.RepairCommand;

class RobotRepairCommandTest {

    @Nested
    class GetCommand {

        @Test
        void shouldReturnRepairCommandInstance() {
            var command = new RobotRepairCommand(List.of());

            var result = command.getCommand();

            assertThat(result).isNotNull().isInstanceOf(RepairCommand.class);
        }
    }

    @Nested
    class GetArgsWithNoFlags {

        @Test
        void shouldReturnEmptyArgsWhenNoFlagsAndNoProperties() {
            var command = new RobotRepairCommand(List.of());

            var args = command.getArgs();

            assertThat(args).isEmpty();
        }

        @Test
        void shouldUseDefaultBehaviorWhenNoFlags() {
            // Default behavior: all available repairs executed by ROBOT
            var command = new RobotRepairCommand(List.of());

            var args = command.getArgs();

            assertThat(args).isEmpty();
        }
    }

    @Nested
    class GetArgsWithInvalidReferencesFlag {

        @Test
        void shouldIncludeInvalidReferencesFlag() {
            var command = new RobotRepairCommand(List.of(), RepairFlags.INVALID_REFERENCES);

            var args = command.getArgs();

            assertThat(args).containsExactly("--invalid-references", "true");
        }

        @Test
        void shouldOmitInvalidReferencesWhenFlagNotProvided() {
            var command = new RobotRepairCommand(List.of());

            var args = command.getArgs();

            assertThat(args).doesNotContain("--invalid-references");
        }
    }

    @Nested
    class GetArgsWithAnnotationProperties {

        @Test
        void shouldNotIncludeAnnotationPropertyWhenListIsEmpty() {
            var command = new RobotRepairCommand(List.of(), RepairFlags.INVALID_REFERENCES);

            var args = command.getArgs();

            assertThat(args).containsExactly("--invalid-references", "true");
        }

        @Test
        void shouldIncludeSingleAnnotationProperty() {
            var command = new RobotRepairCommand(
                    List.of("oboInOwl:hasDbXref"), RepairFlags.INVALID_REFERENCES);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--invalid-references", "true",
                            "--annotation-property", "oboInOwl:hasDbXref");
        }

        @Test
        void shouldIncludeMultipleAnnotationProperties() {
            var command = new RobotRepairCommand(
                    List.of("oboInOwl:hasDbXref", "rdfs:seeAlso", "rdfs:isDefinedBy"),
                    RepairFlags.INVALID_REFERENCES);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--invalid-references", "true",
                            "--annotation-property", "oboInOwl:hasDbXref",
                            "--annotation-property", "rdfs:seeAlso",
                            "--annotation-property", "rdfs:isDefinedBy");
        }

        @Test
        void shouldHandleAnnotationPropertyWithFullIri() {
            var command = new RobotRepairCommand(
                    List.of("http://www.w3.org/2000/01/rdf-schema#seeAlso"),
                    RepairFlags.INVALID_REFERENCES);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--invalid-references", "true",
                            "--annotation-property", "http://www.w3.org/2000/01/rdf-schema#seeAlso");
        }

        @Test
        void shouldHandleMixedCuriesAndIris() {
            var command = new RobotRepairCommand(
                    List.of(
                            "oboInOwl:hasDbXref",
                            "http://www.w3.org/2000/01/rdf-schema#seeAlso",
                            "rdfs:isDefinedBy"),
                    RepairFlags.INVALID_REFERENCES);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--invalid-references", "true",
                            "--annotation-property", "oboInOwl:hasDbXref",
                            "--annotation-property", "http://www.w3.org/2000/01/rdf-schema#seeAlso",
                            "--annotation-property", "rdfs:isDefinedBy");
        }

        @Test
        void shouldIncludePropertiesWithoutFlag() {
            var command = new RobotRepairCommand(
                    List.of("oboInOwl:hasDbXref", "rdfs:seeAlso"));

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--annotation-property", "oboInOwl:hasDbXref",
                            "--annotation-property", "rdfs:seeAlso");
        }
    }

    @Nested
    class GetArgsWithMergeAxiomAnnotationsFlag {

        @Test
        void shouldIncludeMergeAxiomAnnotationsFlag() {
            var command = new RobotRepairCommand(List.of(), RepairFlags.MERGE_AXIOM_ANNOTATIONS);

            var args = command.getArgs();

            assertThat(args).containsExactly("--merge-axiom-annotations", "true");
        }

        @Test
        void shouldOmitMergeAxiomAnnotationsWhenFlagNotProvided() {
            var command = new RobotRepairCommand(List.of());

            var args = command.getArgs();

            assertThat(args).doesNotContain("--merge-axiom-annotations");
        }
    }

    @Nested
    class GetArgsWithMultipleFlags {

        @Test
        void shouldCombineBothFlags() {
            var command = new RobotRepairCommand(
                    List.of(),
                    RepairFlags.INVALID_REFERENCES,
                    RepairFlags.MERGE_AXIOM_ANNOTATIONS);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--invalid-references", "true",
                            "--merge-axiom-annotations", "true");
        }

        @Test
        void shouldCombineFlagsAndProperties() {
            var command = new RobotRepairCommand(
                    List.of("oboInOwl:hasDbXref", "rdfs:seeAlso"),
                    RepairFlags.INVALID_REFERENCES,
                    RepairFlags.MERGE_AXIOM_ANNOTATIONS);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--invalid-references", "true",
                            "--annotation-property", "oboInOwl:hasDbXref",
                            "--annotation-property", "rdfs:seeAlso",
                            "--merge-axiom-annotations", "true");
        }

        @Test
        void shouldHandleComplexScenarioWithFiveProperties() {
            var command = new RobotRepairCommand(
                    List.of(
                            "oboInOwl:hasDbXref",
                            "rdfs:seeAlso",
                            "rdfs:isDefinedBy",
                            "dc:source",
                            "oboInOwl:hasRelatedSynonym"),
                    RepairFlags.INVALID_REFERENCES,
                    RepairFlags.MERGE_AXIOM_ANNOTATIONS);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--invalid-references", "true",
                            "--annotation-property", "oboInOwl:hasDbXref",
                            "--annotation-property", "rdfs:seeAlso",
                            "--annotation-property", "rdfs:isDefinedBy",
                            "--annotation-property", "dc:source",
                            "--annotation-property", "oboInOwl:hasRelatedSynonym",
                            "--merge-axiom-annotations", "true");
        }
    }

    @Nested
    class GetArgsArray {

        @Test
        void shouldConvertToArrayWhenNoArgs() {
            var command = new RobotRepairCommand(List.of());

            var argsArray = command.getArgsArray();

            assertThat(argsArray).isEmpty();
        }

        @Test
        void shouldConvertToArrayWithSingleFlag() {
            var command = new RobotRepairCommand(List.of(), RepairFlags.INVALID_REFERENCES);

            var argsArray = command.getArgsArray();

            assertThat(argsArray).containsExactly("--invalid-references", "true");
        }

        @Test
        void shouldConvertToArrayWithMultipleParameters() {
            var command = new RobotRepairCommand(
                    List.of("oboInOwl:hasDbXref", "rdfs:seeAlso"),
                    RepairFlags.INVALID_REFERENCES,
                    RepairFlags.MERGE_AXIOM_ANNOTATIONS);

            var argsArray = command.getArgsArray();

            assertThat(argsArray)
                    .containsExactly(
                            "--invalid-references", "true",
                            "--annotation-property", "oboInOwl:hasDbXref",
                            "--annotation-property", "rdfs:seeAlso",
                            "--merge-axiom-annotations", "true");
        }
    }

    @Nested
    class Immutability {

        @Test
        void shouldReturnImmutableList() {
            var command = new RobotRepairCommand(
                    List.of("oboInOwl:hasDbXref"),
                    RepairFlags.INVALID_REFERENCES,
                    RepairFlags.MERGE_AXIOM_ANNOTATIONS);

            var args = command.getArgs();

            assertThat(args).isUnmodifiable();
        }

        @Test
        void shouldReturnImmutableListWhenEmpty() {
            var command = new RobotRepairCommand(List.of());

            var args = command.getArgs();

            assertThat(args).isUnmodifiable();
        }
    }

    @Nested
    class RealWorldScenarios {

        @Test
        void shouldHandleBasicDeprecatedClassRepair() {
            // Most common use case: fix deprecated class references without annotation migration
            var command = new RobotRepairCommand(List.of(), RepairFlags.INVALID_REFERENCES);

            var args = command.getArgs();

            assertThat(args).containsExactly("--invalid-references", "true");
        }

        @Test
        void shouldHandleOboDbXrefMigration() {
            // OBO use case: migrate database cross-references when fixing deprecated classes
            var command = new RobotRepairCommand(
                    List.of("oboInOwl:hasDbXref"), RepairFlags.INVALID_REFERENCES);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--invalid-references", "true",
                            "--annotation-property", "oboInOwl:hasDbXref");
        }

        @Test
        void shouldHandleAxiomAnnotationConsolidation() {
            // Consolidate duplicate axioms with different annotations
            var command = new RobotRepairCommand(List.of(), RepairFlags.MERGE_AXIOM_ANNOTATIONS);

            var args = command.getArgs();

            assertThat(args).containsExactly("--merge-axiom-annotations", "true");
        }

        @Test
        void shouldHandleComprehensiveRepair() {
            // Complete repair workflow: fix deprecated references, migrate annotations, merge axioms
            var command = new RobotRepairCommand(
                    List.of("oboInOwl:hasDbXref", "rdfs:seeAlso", "rdfs:isDefinedBy"),
                    RepairFlags.INVALID_REFERENCES,
                    RepairFlags.MERGE_AXIOM_ANNOTATIONS);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--invalid-references", "true",
                            "--annotation-property", "oboInOwl:hasDbXref",
                            "--annotation-property", "rdfs:seeAlso",
                            "--annotation-property", "rdfs:isDefinedBy",
                            "--merge-axiom-annotations", "true");
        }

        @Test
        void shouldHandleAllRepairsWithDefaultSettings() {
            // Default: let ROBOT execute all available repairs
            var command = new RobotRepairCommand(List.of());

            var args = command.getArgs();

            assertThat(args).isEmpty();
        }
    }
}
