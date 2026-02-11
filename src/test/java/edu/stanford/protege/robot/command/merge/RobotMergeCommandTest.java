package edu.stanford.protege.robot.command.merge;

import static org.assertj.core.api.Assertions.assertThat;

import edu.stanford.protege.robot.pipeline.RelativePath;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.obolibrary.robot.MergeCommand;

class RobotMergeCommandTest {

  @Nested
  class GetCommand {

    @Test
    void shouldReturnMergeCommandInstance() {
      var command = new RobotMergeCommand();

      var result = command.getCommand();

      assertThat(result).isNotNull().isInstanceOf(MergeCommand.class);
    }
  }

  @Nested
  class GetArgsWithNoFlags {

    @Test
    void shouldReturnEmptyListWhenNoFlags() {
      var command = new RobotMergeCommand();

      var args = command.getArgs();

      assertThat(args).isEmpty();
    }

    @Test
    void shouldUseDefaultBehaviorWhenNoFlags() {
      // Default behavior:
      // - collapse-import-closure: true (collapse imports into merged ontology)
      // - include-annotations: false (do NOT include ontology annotations)
      // - annotate-derived-from: false (do NOT track provenance via derived-from)
      // - annotate-defined-by: false (do NOT track provenance via defined-by)
      var command = new RobotMergeCommand();

      var args = command.getArgs();

      assertThat(args).isEmpty();
    }
  }

  @Nested
  class GetArgsWithSingleFlag {

    @Test
    void shouldDisableCollapseImportClosureFlag() {
      var command = new RobotMergeCommand(MergeFlags.NO_COLLAPSE_IMPORT_CLOSURE);

      var args = command.getArgs();

      assertThat(args).containsExactly("--collapse-import-closure", "false");
    }

    @Test
    void shouldIncludeAnnotationsFlag() {
      var command = new RobotMergeCommand(MergeFlags.INCLUDE_ANNOTATIONS);

      var args = command.getArgs();

      assertThat(args).containsExactly("--include-annotations", "true");
    }

    @Test
    void shouldAnnotateDerivedFromFlag() {
      var command = new RobotMergeCommand(MergeFlags.ANNOTATE_DERIVED_FROM);

      var args = command.getArgs();

      assertThat(args).containsExactly("--annotate-derived-from", "true");
    }

    @Test
    void shouldAnnotateDefinedByFlag() {
      var command = new RobotMergeCommand(MergeFlags.ANNOTATE_DEFINED_BY);

      var args = command.getArgs();

      assertThat(args).containsExactly("--annotate-defined-by", "true");
    }
  }

  @Nested
  class GetArgsWithMultipleFlags {

    @Test
    void shouldCombineNoCollapseAndIncludeAnnotations() {
      var command = new RobotMergeCommand(
          MergeFlags.NO_COLLAPSE_IMPORT_CLOSURE,
          MergeFlags.INCLUDE_ANNOTATIONS);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--collapse-import-closure", "false",
              "--include-annotations", "true");
    }

    @Test
    void shouldCombineIncludeAnnotationsAndDerivedFrom() {
      var command = new RobotMergeCommand(
          MergeFlags.INCLUDE_ANNOTATIONS,
          MergeFlags.ANNOTATE_DERIVED_FROM);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--include-annotations", "true",
              "--annotate-derived-from", "true");
    }

    @Test
    void shouldCombineDerivedFromAndDefinedBy() {
      var command = new RobotMergeCommand(
          MergeFlags.ANNOTATE_DERIVED_FROM,
          MergeFlags.ANNOTATE_DEFINED_BY);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--annotate-derived-from", "true",
              "--annotate-defined-by", "true");
    }

    @Test
    void shouldCombineAllFourFlags() {
      var command = new RobotMergeCommand(
          MergeFlags.NO_COLLAPSE_IMPORT_CLOSURE,
          MergeFlags.INCLUDE_ANNOTATIONS,
          MergeFlags.ANNOTATE_DERIVED_FROM,
          MergeFlags.ANNOTATE_DEFINED_BY);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--collapse-import-closure", "false",
              "--include-annotations", "true",
              "--annotate-derived-from", "true",
              "--annotate-defined-by", "true");
    }
  }

  @Nested
  class GetArgsWithInputPaths {

    @Test
    void shouldAddSingleInputPath() {
      var inputPaths = List.of(RelativePath.create("ontology2.owl"));
      var command = new RobotMergeCommand(inputPaths);

      var args = command.getArgs();

      assertThat(args).containsExactly("--input", "ontology2.owl");
    }

    @Test
    void shouldAddMultipleInputPaths() {
      var inputPaths = List.of(
          RelativePath.create("ontology2.owl"),
          RelativePath.create("ontology3.owl"));
      var command = new RobotMergeCommand(inputPaths);

      var args = command.getArgs();

      assertThat(args).containsExactly(
          "--input", "ontology2.owl",
          "--input", "ontology3.owl");
    }

    @Test
    void shouldAddInputPathsWithNestedDirectories() {
      var inputPaths = List.of(
          RelativePath.create("imports/external.owl"),
          RelativePath.create("modules/core/base.owl"));
      var command = new RobotMergeCommand(inputPaths);

      var args = command.getArgs();

      assertThat(args).containsExactly(
          "--input", "imports/external.owl",
          "--input", "modules/core/base.owl");
    }

    @Test
    void shouldHandleEmptyInputPathsList() {
      var inputPaths = List.<RelativePath>of();
      var command = new RobotMergeCommand(inputPaths);

      var args = command.getArgs();

      assertThat(args).isEmpty();
    }

    @Test
    void shouldHandleNullInputPaths() {
      List<RelativePath> nullPaths = null;
      var command = new RobotMergeCommand(nullPaths, MergeFlags.INCLUDE_ANNOTATIONS);

      var args = command.getArgs();

      assertThat(args).containsExactly("--include-annotations", "true");
    }
  }

  @Nested
  class GetArgsWithInputPathsAndFlags {

    @Test
    void shouldCombineInputPathsAndSingleFlag() {
      var inputPaths = List.of(RelativePath.create("ontology2.owl"));
      var command = new RobotMergeCommand(inputPaths, MergeFlags.INCLUDE_ANNOTATIONS);

      var args = command.getArgs();

      assertThat(args).containsExactly(
          "--input", "ontology2.owl",
          "--include-annotations", "true");
    }

    @Test
    void shouldCombineMultipleInputPathsAndMultipleFlags() {
      var inputPaths = List.of(
          RelativePath.create("ontology2.owl"),
          RelativePath.create("ontology3.owl"));
      var command = new RobotMergeCommand(
          inputPaths,
          MergeFlags.INCLUDE_ANNOTATIONS,
          MergeFlags.ANNOTATE_DERIVED_FROM);

      var args = command.getArgs();

      assertThat(args).containsExactly(
          "--input", "ontology2.owl",
          "--input", "ontology3.owl",
          "--include-annotations", "true",
          "--annotate-derived-from", "true");
    }

    @Test
    void shouldPutInputPathsBeforeFlags() {
      var inputPaths = List.of(RelativePath.create("other.owl"));
      var command = new RobotMergeCommand(
          inputPaths,
          MergeFlags.NO_COLLAPSE_IMPORT_CLOSURE,
          MergeFlags.INCLUDE_ANNOTATIONS,
          MergeFlags.ANNOTATE_DERIVED_FROM,
          MergeFlags.ANNOTATE_DEFINED_BY);

      var args = command.getArgs();

      // Verify input paths come first, then flags
      assertThat(args.get(0)).isEqualTo("--input");
      assertThat(args.get(1)).isEqualTo("other.owl");
      assertThat(args.subList(2, args.size())).containsExactly(
          "--collapse-import-closure", "false",
          "--include-annotations", "true",
          "--annotate-derived-from", "true",
          "--annotate-defined-by", "true");
    }
  }

  @Nested
  class GetArgsArray {

    @Test
    void shouldConvertToArrayWithNoFlags() {
      var command = new RobotMergeCommand();

      var argsArray = command.getArgsArray();

      assertThat(argsArray).isEmpty();
    }

    @Test
    void shouldConvertToArrayWithSingleFlag() {
      var command = new RobotMergeCommand(MergeFlags.INCLUDE_ANNOTATIONS);

      var argsArray = command.getArgsArray();

      assertThat(argsArray).containsExactly("--include-annotations", "true");
    }

    @Test
    void shouldConvertToArrayWithMultipleFlags() {
      var command = new RobotMergeCommand(
          MergeFlags.NO_COLLAPSE_IMPORT_CLOSURE,
          MergeFlags.INCLUDE_ANNOTATIONS,
          MergeFlags.ANNOTATE_DERIVED_FROM,
          MergeFlags.ANNOTATE_DEFINED_BY);

      var argsArray = command.getArgsArray();

      assertThat(argsArray)
          .containsExactly(
              "--collapse-import-closure", "false",
              "--include-annotations", "true",
              "--annotate-derived-from", "true",
              "--annotate-defined-by", "true");
    }

    @Test
    void shouldConvertToArrayWithInputPathsAndFlags() {
      var inputPaths = List.of(RelativePath.create("ontology2.owl"));
      var command = new RobotMergeCommand(inputPaths, MergeFlags.INCLUDE_ANNOTATIONS);

      var argsArray = command.getArgsArray();

      assertThat(argsArray).containsExactly(
          "--input", "ontology2.owl",
          "--include-annotations", "true");
    }
  }

  @Nested
  class Immutability {

    @Test
    void shouldReturnImmutableList() {
      var command = new RobotMergeCommand(
          MergeFlags.INCLUDE_ANNOTATIONS,
          MergeFlags.ANNOTATE_DERIVED_FROM);

      var args = command.getArgs();

      assertThat(args).isUnmodifiable();
    }

    @Test
    void shouldReturnImmutableListWhenEmpty() {
      var command = new RobotMergeCommand();

      var args = command.getArgs();

      assertThat(args).isUnmodifiable();
    }

    @Test
    void shouldReturnImmutableListWithInputPaths() {
      var inputPaths = List.of(RelativePath.create("ontology2.owl"));
      var command = new RobotMergeCommand(inputPaths, MergeFlags.INCLUDE_ANNOTATIONS);

      var args = command.getArgs();

      assertThat(args).isUnmodifiable();
    }
  }

  @Nested
  class RealWorldScenarios {

    @Test
    void shouldHandleBasicMerge() {
      // Most common use case: basic merge with defaults (collapse imports)
      var command = new RobotMergeCommand();

      var args = command.getArgs();

      assertThat(args).isEmpty();
    }

    @Test
    void shouldHandleMergeWithoutCollapsingImports() {
      // Keep imports separate rather than collapsing them
      var command = new RobotMergeCommand(MergeFlags.NO_COLLAPSE_IMPORT_CLOSURE);

      var args = command.getArgs();

      assertThat(args).containsExactly("--collapse-import-closure", "false");
    }

    @Test
    void shouldHandleMergeWithAnnotationsAndProvenance() {
      // Full provenance tracking: include annotations and track origins
      var command = new RobotMergeCommand(
          MergeFlags.INCLUDE_ANNOTATIONS,
          MergeFlags.ANNOTATE_DERIVED_FROM,
          MergeFlags.ANNOTATE_DEFINED_BY);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--include-annotations", "true",
              "--annotate-derived-from", "true",
              "--annotate-defined-by", "true");
    }

    @Test
    void shouldHandleCompleteMergeWorkflow() {
      // Complete merge with all options enabled
      var command = new RobotMergeCommand(
          MergeFlags.NO_COLLAPSE_IMPORT_CLOSURE,
          MergeFlags.INCLUDE_ANNOTATIONS,
          MergeFlags.ANNOTATE_DERIVED_FROM,
          MergeFlags.ANNOTATE_DEFINED_BY);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--collapse-import-closure", "false",
              "--include-annotations", "true",
              "--annotate-derived-from", "true",
              "--annotate-defined-by", "true");
    }

    @Test
    void shouldHandleMergingMultipleOntologyModules() {
      // Merging modular ontology: base + multiple modules with provenance
      var inputPaths = List.of(
          RelativePath.create("modules/anatomy.owl"),
          RelativePath.create("modules/phenotype.owl"),
          RelativePath.create("modules/disease.owl"));
      var command = new RobotMergeCommand(
          inputPaths,
          MergeFlags.INCLUDE_ANNOTATIONS,
          MergeFlags.ANNOTATE_DERIVED_FROM);

      var args = command.getArgs();

      assertThat(args).containsExactly(
          "--input", "modules/anatomy.owl",
          "--input", "modules/phenotype.owl",
          "--input", "modules/disease.owl",
          "--include-annotations", "true",
          "--annotate-derived-from", "true");
    }

    @Test
    void shouldHandleMergingExternalImports() {
      // Merging external ontology imports into a single file
      var inputPaths = List.of(
          RelativePath.create("imports/go.owl"),
          RelativePath.create("imports/uberon.owl"));
      var command = new RobotMergeCommand(
          inputPaths,
          MergeFlags.NO_COLLAPSE_IMPORT_CLOSURE,
          MergeFlags.ANNOTATE_DEFINED_BY);

      var args = command.getArgs();

      assertThat(args).containsExactly(
          "--input", "imports/go.owl",
          "--input", "imports/uberon.owl",
          "--collapse-import-closure", "false",
          "--annotate-defined-by", "true");
    }
  }
}
