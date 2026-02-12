package edu.stanford.protege.robot.command.materialize;

import static org.assertj.core.api.Assertions.assertThat;

import edu.stanford.protege.robot.command.common.Reasoner;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.obolibrary.robot.MaterializeCommand;

class RobotMaterializeCommandTest {

    @Nested
    class GetCommand {

        @Test
        void shouldReturnMaterializeCommandInstance() {
            var command = new RobotMaterializeCommand(null, List.of());

            var result = command.getCommand();

            assertThat(result).isNotNull().isInstanceOf(MaterializeCommand.class);
        }
    }

    @Nested
    class GetArgsWithReasoner {

        @Test
        void shouldOmitReasonerWhenNull() {
            var command = new RobotMaterializeCommand(null, List.of());

            var args = command.getArgs();

            assertThat(args).doesNotContain("--reasoner");
        }

        @Test
        void shouldIncludeElkReasoner() {
            var command = new RobotMaterializeCommand(Reasoner.ELK, List.of());

            var args = command.getArgs();

            assertThat(args).containsExactly("--reasoner", "ELK");
        }

        @Test
        void shouldIncludeHermiTReasoner() {
            var command = new RobotMaterializeCommand(Reasoner.HERMIT, List.of());

            var args = command.getArgs();

            assertThat(args).containsExactly("--reasoner", "HermiT");
        }
    }

    @Nested
    class GetArgsWithTerms {

        @Test
        void shouldNotIncludeTermWhenListIsEmpty() {
            var command = new RobotMaterializeCommand(null, List.of());

            var args = command.getArgs();

            assertThat(args).doesNotContain("--term");
        }

        @Test
        void shouldIncludeSingleTerm() {
            var command = new RobotMaterializeCommand(null, List.of("BFO:0000050"));

            var args = command.getArgs();

            assertThat(args).containsExactly("--term", "BFO:0000050");
        }

        @Test
        void shouldIncludeMultipleTerms() {
            var command = new RobotMaterializeCommand(
                    null, List.of("BFO:0000050", "BFO:0000051", "RO:0002211"));

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--term", "BFO:0000050",
                            "--term", "BFO:0000051",
                            "--term", "RO:0002211");
        }

        @Test
        void shouldHandleTermWithFullIri() {
            var command = new RobotMaterializeCommand(
                    null, List.of("http://purl.obolibrary.org/obo/BFO_0000050"));

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly("--term", "http://purl.obolibrary.org/obo/BFO_0000050");
        }

        @Test
        void shouldHandleMixedCuriesAndIris() {
            var command = new RobotMaterializeCommand(
                    null,
                    List.of(
                            "BFO:0000050",
                            "http://purl.obolibrary.org/obo/RO_0002211",
                            "BFO:0000051"));

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--term", "BFO:0000050",
                            "--term", "http://purl.obolibrary.org/obo/RO_0002211",
                            "--term", "BFO:0000051");
        }
    }

    @Nested
    class GetArgsWithRemoveRedundantSubclassAxiomsFlag {

        @Test
        void shouldIncludeRemoveRedundantSubclassAxiomsFlag() {
            var command = new RobotMaterializeCommand(
                    null, List.of(), MaterializeFlags.REMOVE_REDUNDANT_SUBCLASS_AXIOMS);

            var args = command.getArgs();

            assertThat(args).containsExactly("--remove-redundant-subclass-axioms", "true");
        }

        @Test
        void shouldOmitRemoveRedundantSubclassAxiomsWhenFlagNotProvided() {
            var command = new RobotMaterializeCommand(null, List.of());

            var args = command.getArgs();

            assertThat(args).doesNotContain("--remove-redundant-subclass-axioms");
        }
    }

    @Nested
    class GetArgsWithCreateNewOntologyFlag {

        @Test
        void shouldIncludeCreateNewOntologyFlag() {
            var command = new RobotMaterializeCommand(
                    null, List.of(), MaterializeFlags.CREATE_NEW_ONTOLOGY);

            var args = command.getArgs();

            assertThat(args).containsExactly("--create-new-ontology", "true");
        }

        @Test
        void shouldOmitCreateNewOntologyWhenFlagNotProvided() {
            var command = new RobotMaterializeCommand(null, List.of());

            var args = command.getArgs();

            assertThat(args).doesNotContain("--create-new-ontology");
        }
    }

    @Nested
    class GetArgsWithAnnotateInferredAxiomsFlag {

        @Test
        void shouldIncludeAnnotateInferredAxiomsFlag() {
            var command = new RobotMaterializeCommand(
                    null, List.of(), MaterializeFlags.ANNOTATE_INFERRED_AXIOMS);

            var args = command.getArgs();

            assertThat(args).containsExactly("--annotate-inferred-axioms", "true");
        }

        @Test
        void shouldOmitAnnotateInferredAxiomsWhenFlagNotProvided() {
            var command = new RobotMaterializeCommand(null, List.of());

            var args = command.getArgs();

            assertThat(args).doesNotContain("--annotate-inferred-axioms");
        }
    }

    @Nested
    class GetArgsWithCombinedParameters {

        @Test
        void shouldReturnEmptyArgsWhenAllDefaults() {
            var command = new RobotMaterializeCommand(null, List.of());

            var args = command.getArgs();

            assertThat(args).isEmpty();
        }

        @Test
        void shouldCombineReasonerAndTerms() {
            var command = new RobotMaterializeCommand(
                    Reasoner.ELK, List.of("BFO:0000050", "BFO:0000051"));

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--reasoner", "ELK",
                            "--term", "BFO:0000050",
                            "--term", "BFO:0000051");
        }

        @Test
        void shouldCombineReasonerAndFlags() {
            var command = new RobotMaterializeCommand(
                    Reasoner.HERMIT, List.of(),
                    MaterializeFlags.REMOVE_REDUNDANT_SUBCLASS_AXIOMS,
                    MaterializeFlags.ANNOTATE_INFERRED_AXIOMS);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--reasoner", "HermiT",
                            "--remove-redundant-subclass-axioms", "true",
                            "--annotate-inferred-axioms", "true");
        }

        @Test
        void shouldCombineTermsAndFlags() {
            var command = new RobotMaterializeCommand(
                    null, List.of("BFO:0000050"),
                    MaterializeFlags.CREATE_NEW_ONTOLOGY);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--term", "BFO:0000050",
                            "--create-new-ontology", "true");
        }

        @Test
        void shouldCombineAllParameters() {
            var command = new RobotMaterializeCommand(
                    Reasoner.ELK,
                    List.of("BFO:0000050", "RO:0002211"),
                    MaterializeFlags.REMOVE_REDUNDANT_SUBCLASS_AXIOMS,
                    MaterializeFlags.CREATE_NEW_ONTOLOGY,
                    MaterializeFlags.ANNOTATE_INFERRED_AXIOMS);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--reasoner", "ELK",
                            "--term", "BFO:0000050",
                            "--term", "RO:0002211",
                            "--remove-redundant-subclass-axioms", "true",
                            "--create-new-ontology", "true",
                            "--annotate-inferred-axioms", "true");
        }

        @Test
        void shouldCombineCreateNewOntologyAndAnnotateInferredAxioms() {
            var command = new RobotMaterializeCommand(
                    null, List.of(),
                    MaterializeFlags.CREATE_NEW_ONTOLOGY,
                    MaterializeFlags.ANNOTATE_INFERRED_AXIOMS);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--create-new-ontology", "true",
                            "--annotate-inferred-axioms", "true");
        }
    }

    @Nested
    class GetArgsArray {

        @Test
        void shouldConvertArgsListToArray() {
            var command = new RobotMaterializeCommand(
                    Reasoner.ELK, List.of("BFO:0000050"),
                    MaterializeFlags.REMOVE_REDUNDANT_SUBCLASS_AXIOMS);

            var argsArray = command.getArgsArray();

            assertThat(argsArray)
                    .isInstanceOf(String[].class)
                    .containsExactly(
                            "--reasoner", "ELK",
                            "--term", "BFO:0000050",
                            "--remove-redundant-subclass-axioms", "true");
        }

        @Test
        void shouldReturnEmptyArrayWhenNoArgs() {
            var command = new RobotMaterializeCommand(null, List.of());

            var argsArray = command.getArgsArray();

            assertThat(argsArray).isEmpty();
        }

        @Test
        void shouldConvertReasonerOnlyToArray() {
            var command = new RobotMaterializeCommand(Reasoner.HERMIT, List.of());

            var argsArray = command.getArgsArray();

            assertThat(argsArray).containsExactly("--reasoner", "HermiT");
        }
    }

    @Nested
    class Immutability {

        @Test
        void shouldReturnImmutableList() {
            var command = new RobotMaterializeCommand(
                    Reasoner.ELK,
                    List.of("BFO:0000050"),
                    MaterializeFlags.REMOVE_REDUNDANT_SUBCLASS_AXIOMS);

            var args = command.getArgs();

            assertThat(args).isUnmodifiable();
        }

        @Test
        void shouldReturnImmutableListWhenEmpty() {
            var command = new RobotMaterializeCommand(null, List.of());

            var args = command.getArgs();

            assertThat(args).isUnmodifiable();
        }
    }

    @Nested
    class RealWorldScenarios {

        @Test
        void shouldHandleBasicElkMaterialization() {
            // Most common use case: materialize all properties using ELK
            var command = new RobotMaterializeCommand(Reasoner.ELK, List.of());

            var args = command.getArgs();

            assertThat(args).containsExactly("--reasoner", "ELK");
        }

        @Test
        void shouldHandleSpecificPropertyMaterialization() {
            // Materialize only part_of and has_part relationships
            var command = new RobotMaterializeCommand(
                    Reasoner.ELK, List.of("BFO:0000050", "BFO:0000051"));

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--reasoner", "ELK",
                            "--term", "BFO:0000050",
                            "--term", "BFO:0000051");
        }

        @Test
        void shouldHandleNewOntologyCreation() {
            // Create a new ontology with only materialized axioms
            var command = new RobotMaterializeCommand(
                    Reasoner.ELK, List.of("BFO:0000050"),
                    MaterializeFlags.CREATE_NEW_ONTOLOGY);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--reasoner", "ELK",
                            "--term", "BFO:0000050",
                            "--create-new-ontology", "true");
        }

        @Test
        void shouldHandleComprehensiveWorkflow() {
            // Full materialization workflow: ELK reasoner, specific properties, all flags
            var command = new RobotMaterializeCommand(
                    Reasoner.ELK,
                    List.of("BFO:0000050", "BFO:0000051", "RO:0002211"),
                    MaterializeFlags.REMOVE_REDUNDANT_SUBCLASS_AXIOMS,
                    MaterializeFlags.CREATE_NEW_ONTOLOGY,
                    MaterializeFlags.ANNOTATE_INFERRED_AXIOMS);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--reasoner", "ELK",
                            "--term", "BFO:0000050",
                            "--term", "BFO:0000051",
                            "--term", "RO:0002211",
                            "--remove-redundant-subclass-axioms", "true",
                            "--create-new-ontology", "true",
                            "--annotate-inferred-axioms", "true");
        }
    }
}
