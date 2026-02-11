package edu.stanford.protege.robot.command.reduce;

import static org.assertj.core.api.Assertions.assertThat;

import edu.stanford.protege.robot.command.common.Reasoner;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.obolibrary.robot.ReduceCommand;

class RobotReduceCommandTest {

    @Nested
    class GetCommand {

        @Test
        void shouldReturnReduceCommandInstance() {
            var command = new RobotReduceCommand(Reasoner.ELK);

            var result = command.getCommand();

            assertThat(result).isNotNull().isInstanceOf(ReduceCommand.class);
        }
    }

    @Nested
    class GetArgsWithNoFlags {

        @Test
        void shouldReturnReasonerArgOnly() {
            var command = new RobotReduceCommand(Reasoner.ELK);

            var args = command.getArgs();

            assertThat(args).containsExactly("--reasoner", "ELK");
        }

        @Test
        void shouldUseDefaultBehaviorWhenNoFlags() {
            // Default behavior:
            // - preserve-annotated-axioms: false (remove redundant axioms even if annotated)
            // - named-classes-only: false (check all classes including anonymous)
            // - include-subproperties: false (do not factor subproperties)
            var command = new RobotReduceCommand(Reasoner.ELK);

            var args = command.getArgs();

            assertThat(args).containsExactly("--reasoner", "ELK");
        }
    }

    @Nested
    class GetArgsWithDifferentReasoners {

        @Test
        void shouldUseElkReasoner() {
            var command = new RobotReduceCommand(Reasoner.ELK);

            var args = command.getArgs();

            assertThat(args).containsExactly("--reasoner", "ELK");
        }

        @Test
        void shouldUseHermitReasoner() {
            var command = new RobotReduceCommand(Reasoner.HERMIT);

            var args = command.getArgs();

            assertThat(args).containsExactly("--reasoner", "HermiT");
        }

        @Test
        void shouldUseJFactReasoner() {
            var command = new RobotReduceCommand(Reasoner.JFACT);

            var args = command.getArgs();

            assertThat(args).containsExactly("--reasoner", "JFact");
        }

        @Test
        void shouldUseWhelkReasoner() {
            var command = new RobotReduceCommand(Reasoner.WHELK);

            var args = command.getArgs();

            assertThat(args).containsExactly("--reasoner", "whelk");
        }

        @Test
        void shouldUseStructuralReasoner() {
            var command = new RobotReduceCommand(Reasoner.STRUCTURAL);

            var args = command.getArgs();

            assertThat(args).containsExactly("--reasoner", "structural");
        }
    }

    @Nested
    class GetArgsWithSingleFlag {

        @Test
        void shouldIncludePreserveAnnotatedAxiomsFlag() {
            var command = new RobotReduceCommand(
                    Reasoner.ELK,
                    ReduceFlags.PRESERVE_ANNOTATED_AXIOMS);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--reasoner", "ELK",
                            "--preserve-annotated-axioms", "true");
        }

        @Test
        void shouldIncludeNamedClassesOnlyFlag() {
            var command = new RobotReduceCommand(
                    Reasoner.ELK,
                    ReduceFlags.NAMED_CLASSES_ONLY);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--reasoner", "ELK",
                            "--named-classes-only", "true");
        }

        @Test
        void shouldIncludeSubpropertiesFlag() {
            var command = new RobotReduceCommand(
                    Reasoner.ELK,
                    ReduceFlags.INCLUDE_SUBPROPERTIES);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--reasoner", "ELK",
                            "--include-subproperties", "true");
        }
    }

    @Nested
    class GetArgsWithMultipleFlags {

        @Test
        void shouldCombinePreserveAnnotatedAxiomsAndNamedClassesOnly() {
            var command = new RobotReduceCommand(
                    Reasoner.ELK,
                    ReduceFlags.PRESERVE_ANNOTATED_AXIOMS,
                    ReduceFlags.NAMED_CLASSES_ONLY);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--reasoner", "ELK",
                            "--preserve-annotated-axioms", "true",
                            "--named-classes-only", "true");
        }

        @Test
        void shouldCombinePreserveAnnotatedAxiomsAndIncludeSubproperties() {
            var command = new RobotReduceCommand(
                    Reasoner.ELK,
                    ReduceFlags.PRESERVE_ANNOTATED_AXIOMS,
                    ReduceFlags.INCLUDE_SUBPROPERTIES);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--reasoner", "ELK",
                            "--preserve-annotated-axioms", "true",
                            "--include-subproperties", "true");
        }

        @Test
        void shouldCombineNamedClassesOnlyAndIncludeSubproperties() {
            var command = new RobotReduceCommand(
                    Reasoner.ELK,
                    ReduceFlags.NAMED_CLASSES_ONLY,
                    ReduceFlags.INCLUDE_SUBPROPERTIES);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--reasoner", "ELK",
                            "--named-classes-only", "true",
                            "--include-subproperties", "true");
        }

        @Test
        void shouldCombineAllThreeFlags() {
            var command = new RobotReduceCommand(
                    Reasoner.ELK,
                    ReduceFlags.PRESERVE_ANNOTATED_AXIOMS,
                    ReduceFlags.NAMED_CLASSES_ONLY,
                    ReduceFlags.INCLUDE_SUBPROPERTIES);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--reasoner", "ELK",
                            "--preserve-annotated-axioms", "true",
                            "--named-classes-only", "true",
                            "--include-subproperties", "true");
        }
    }

    @Nested
    class GetArgsArray {

        @Test
        void shouldConvertToArrayWithReasonerOnly() {
            var command = new RobotReduceCommand(Reasoner.ELK);

            var argsArray = command.getArgsArray();

            assertThat(argsArray).containsExactly("--reasoner", "ELK");
        }

        @Test
        void shouldConvertToArrayWithSingleFlag() {
            var command = new RobotReduceCommand(
                    Reasoner.ELK,
                    ReduceFlags.PRESERVE_ANNOTATED_AXIOMS);

            var argsArray = command.getArgsArray();

            assertThat(argsArray)
                    .containsExactly(
                            "--reasoner", "ELK",
                            "--preserve-annotated-axioms", "true");
        }

        @Test
        void shouldConvertToArrayWithMultipleFlags() {
            var command = new RobotReduceCommand(
                    Reasoner.ELK,
                    ReduceFlags.PRESERVE_ANNOTATED_AXIOMS,
                    ReduceFlags.NAMED_CLASSES_ONLY,
                    ReduceFlags.INCLUDE_SUBPROPERTIES);

            var argsArray = command.getArgsArray();

            assertThat(argsArray)
                    .containsExactly(
                            "--reasoner", "ELK",
                            "--preserve-annotated-axioms", "true",
                            "--named-classes-only", "true",
                            "--include-subproperties", "true");
        }
    }

    @Nested
    class Immutability {

        @Test
        void shouldReturnImmutableList() {
            var command = new RobotReduceCommand(
                    Reasoner.ELK,
                    ReduceFlags.PRESERVE_ANNOTATED_AXIOMS,
                    ReduceFlags.NAMED_CLASSES_ONLY);

            var args = command.getArgs();

            assertThat(args).isUnmodifiable();
        }

        @Test
        void shouldReturnImmutableListWithReasonerOnly() {
            var command = new RobotReduceCommand(Reasoner.ELK);

            var args = command.getArgs();

            assertThat(args).isUnmodifiable();
        }
    }

    @Nested
    class RealWorldScenarios {

        @Test
        void shouldHandleBasicReduction() {
            // Most common use case: basic reduction with ELK reasoner
            var command = new RobotReduceCommand(Reasoner.ELK);

            var args = command.getArgs();

            assertThat(args).containsExactly("--reasoner", "ELK");
        }

        @Test
        void shouldHandlePreservingAnnotations() {
            // Preserve annotated axioms during reduction
            var command = new RobotReduceCommand(
                    Reasoner.ELK,
                    ReduceFlags.PRESERVE_ANNOTATED_AXIOMS);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--reasoner", "ELK",
                            "--preserve-annotated-axioms", "true");
        }

        @Test
        void shouldHandleHermitForFullOwl() {
            // Use HermiT for ontologies requiring full OWL DL reasoning
            var command = new RobotReduceCommand(Reasoner.HERMIT);

            var args = command.getArgs();

            assertThat(args).containsExactly("--reasoner", "HermiT");
        }

        @Test
        void shouldHandleComprehensiveReduction() {
            // Full reduction with all options enabled
            var command = new RobotReduceCommand(
                    Reasoner.ELK,
                    ReduceFlags.PRESERVE_ANNOTATED_AXIOMS,
                    ReduceFlags.NAMED_CLASSES_ONLY,
                    ReduceFlags.INCLUDE_SUBPROPERTIES);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--reasoner", "ELK",
                            "--preserve-annotated-axioms", "true",
                            "--named-classes-only", "true",
                            "--include-subproperties", "true");
        }

        @Test
        void shouldHandleRelaxReduceWorkflow() {
            // Reduce step in a relax-reduce workflow
            // After relax generates SubClassOf axioms, reduce removes redundant ones
            var command = new RobotReduceCommand(
                    Reasoner.ELK,
                    ReduceFlags.NAMED_CLASSES_ONLY);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--reasoner", "ELK",
                            "--named-classes-only", "true");
        }
    }
}
