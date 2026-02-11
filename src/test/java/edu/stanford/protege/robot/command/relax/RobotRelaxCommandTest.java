package edu.stanford.protege.robot.command.relax;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.obolibrary.robot.RelaxCommand;

class RobotRelaxCommandTest {

    @Nested
    class GetCommand {

        @Test
        void shouldReturnRelaxCommandInstance() {
            var command = new RobotRelaxCommand();

            var result = command.getCommand();

            assertThat(result).isNotNull().isInstanceOf(RelaxCommand.class);
        }
    }

    @Nested
    class GetArgsWithNoFlags {

        @Test
        void shouldReturnEmptyListWhenNoFlags() {
            var command = new RobotRelaxCommand();

            var args = command.getArgs();

            assertThat(args).isEmpty();
        }

        @Test
        void shouldUseDefaultBehaviorWhenNoFlags() {
            // Default behavior:
            // - exclude-named-classes: true (do NOT relax named class equivalences)
            // - include-subclass-of: false (do NOT relax SubClassOf axioms)
            // - enforce-obo-format: false (do NOT enforce OBO restrictions)
            var command = new RobotRelaxCommand();

            var args = command.getArgs();

            assertThat(args).isEmpty();
        }
    }

    @Nested
    class GetArgsWithSingleFlag {

        @Test
        void shouldIncludeNamedClassesFlag() {
            var command = new RobotRelaxCommand(RelaxFlags.INCLUDE_NAMED_CLASSES);

            var args = command.getArgs();

            assertThat(args).containsExactly("--exclude-named-classes", "false");
        }

        @Test
        void shouldIncludeSubClassOfFlag() {
            var command = new RobotRelaxCommand(RelaxFlags.INCLUDE_SUBCLASS_OF);

            var args = command.getArgs();

            assertThat(args).containsExactly("--include-subclass-of", "true");
        }

        @Test
        void shouldEnforceOboFormatFlag() {
            var command = new RobotRelaxCommand(RelaxFlags.ENFORCE_OBO_FORMAT);

            var args = command.getArgs();

            assertThat(args).containsExactly("--enforce-obo-format", "true");
        }
    }

    @Nested
    class GetArgsWithMultipleFlags {

        @Test
        void shouldCombineIncludeNamedClassesAndSubClassOf() {
            var command = new RobotRelaxCommand(
                    RelaxFlags.INCLUDE_NAMED_CLASSES,
                    RelaxFlags.INCLUDE_SUBCLASS_OF);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--exclude-named-classes", "false",
                            "--include-subclass-of", "true");
        }

        @Test
        void shouldCombineIncludeNamedClassesAndEnforceOboFormat() {
            var command = new RobotRelaxCommand(
                    RelaxFlags.INCLUDE_NAMED_CLASSES,
                    RelaxFlags.ENFORCE_OBO_FORMAT);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--exclude-named-classes", "false",
                            "--enforce-obo-format", "true");
        }

        @Test
        void shouldCombineIncludeSubClassOfAndEnforceOboFormat() {
            var command = new RobotRelaxCommand(
                    RelaxFlags.INCLUDE_SUBCLASS_OF,
                    RelaxFlags.ENFORCE_OBO_FORMAT);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--include-subclass-of", "true",
                            "--enforce-obo-format", "true");
        }

        @Test
        void shouldCombineAllThreeFlags() {
            var command = new RobotRelaxCommand(
                    RelaxFlags.INCLUDE_NAMED_CLASSES,
                    RelaxFlags.INCLUDE_SUBCLASS_OF,
                    RelaxFlags.ENFORCE_OBO_FORMAT);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--exclude-named-classes", "false",
                            "--include-subclass-of", "true",
                            "--enforce-obo-format", "true");
        }
    }

    @Nested
    class GetArgsArray {

        @Test
        void shouldConvertToArrayWithNoFlags() {
            var command = new RobotRelaxCommand();

            var argsArray = command.getArgsArray();

            assertThat(argsArray).isEmpty();
        }

        @Test
        void shouldConvertToArrayWithSingleFlag() {
            var command = new RobotRelaxCommand(RelaxFlags.INCLUDE_NAMED_CLASSES);

            var argsArray = command.getArgsArray();

            assertThat(argsArray).containsExactly("--exclude-named-classes", "false");
        }

        @Test
        void shouldConvertToArrayWithMultipleFlags() {
            var command = new RobotRelaxCommand(
                    RelaxFlags.INCLUDE_NAMED_CLASSES,
                    RelaxFlags.INCLUDE_SUBCLASS_OF,
                    RelaxFlags.ENFORCE_OBO_FORMAT);

            var argsArray = command.getArgsArray();

            assertThat(argsArray)
                    .containsExactly(
                            "--exclude-named-classes", "false",
                            "--include-subclass-of", "true",
                            "--enforce-obo-format", "true");
        }
    }

    @Nested
    class Immutability {

        @Test
        void shouldReturnImmutableList() {
            var command = new RobotRelaxCommand(
                    RelaxFlags.INCLUDE_NAMED_CLASSES,
                    RelaxFlags.INCLUDE_SUBCLASS_OF);

            var args = command.getArgs();

            assertThat(args).isUnmodifiable();
        }

        @Test
        void shouldReturnImmutableListWhenEmpty() {
            var command = new RobotRelaxCommand();

            var args = command.getArgs();

            assertThat(args).isUnmodifiable();
        }
    }

    @Nested
    class RealWorldScenarios {

        @Test
        void shouldHandleBasicRelaxation() {
            // Most common use case: basic relaxation with defaults
            // Excludes named class equivalences, only relaxes EquivalentTo axioms
            var command = new RobotRelaxCommand();

            var args = command.getArgs();

            assertThat(args).isEmpty();
        }

        @Test
        void shouldHandleFullRelaxationForGraphApplications() {
            // For graph-based applications: include all possible SubClassOf axioms
            var command = new RobotRelaxCommand(
                    RelaxFlags.INCLUDE_NAMED_CLASSES,
                    RelaxFlags.INCLUDE_SUBCLASS_OF);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--exclude-named-classes", "false",
                            "--include-subclass-of", "true");
        }

        @Test
        void shouldHandleOboFormatConversion() {
            // For OBO format output: enforce OBO restrictions during relaxation
            var command = new RobotRelaxCommand(RelaxFlags.ENFORCE_OBO_FORMAT);

            var args = command.getArgs();

            assertThat(args).containsExactly("--enforce-obo-format", "true");
        }

        @Test
        void shouldHandleCompleteOboWorkflow() {
            // Complete workflow for OBO format: include everything and enforce OBO
            var command = new RobotRelaxCommand(
                    RelaxFlags.INCLUDE_NAMED_CLASSES,
                    RelaxFlags.INCLUDE_SUBCLASS_OF,
                    RelaxFlags.ENFORCE_OBO_FORMAT);

            var args = command.getArgs();

            assertThat(args)
                    .containsExactly(
                            "--exclude-named-classes", "false",
                            "--include-subclass-of", "true",
                            "--enforce-obo-format", "true");
        }
    }
}
