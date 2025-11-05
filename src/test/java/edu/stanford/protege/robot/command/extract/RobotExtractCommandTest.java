package edu.stanford.protege.robot.command.extract;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import edu.stanford.protege.robot.command.extract.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.obolibrary.robot.ExtractCommand;

class RobotExtractCommandTest {

  @Nested
  class GetCommand {

    @Test
    void shouldReturnExtractCommandInstance() {
      var strategy = new SlmeExtractStrategy(SlmeExtractMethod.BOT, List.of("GO:0008150"));
      var command = new RobotExtractCommand(strategy, null, null, null);

      var result = command.getCommand();

      assertThat(result)
          .isNotNull()
          .isInstanceOf(ExtractCommand.class);
    }
  }

  @Nested
  class GetArgsWithNullOptions {

    @Test
    void shouldGenerateMinimalArgs() {
      var strategy = new SlmeExtractStrategy(SlmeExtractMethod.BOT, List.of("GO:0008150"));
      var command = new RobotExtractCommand(strategy, null, null, null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly("--method", "BOT", "--term", "GO:0008150");
    }
  }

  @Nested
  class GetArgsWithIntermediates {

    @Test
    void shouldIncludeIntermediatesAll() {
      var strategy = new SlmeExtractStrategy(SlmeExtractMethod.BOT, List.of("GO:0008150"));
      var command = new RobotExtractCommand(strategy, ExtractIntermediates.all, null, null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly("--method", "BOT", "--term", "GO:0008150", "--intermediates", "all");
    }

    @Test
    void shouldIncludeIntermediatesMinimal() {
      var strategy = new SlmeExtractStrategy(SlmeExtractMethod.BOT, List.of("GO:0008150"));
      var command = new RobotExtractCommand(strategy, ExtractIntermediates.minimal, null, null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--method", "BOT", "--term", "GO:0008150", "--intermediates", "minimal");
    }

    @Test
    void shouldIncludeIntermediatesNone() {
      var strategy = new SlmeExtractStrategy(SlmeExtractMethod.BOT, List.of("GO:0008150"));
      var command = new RobotExtractCommand(strategy, ExtractIntermediates.none, null, null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly("--method", "BOT", "--term", "GO:0008150", "--intermediates", "none");
    }
  }

  @Nested
  class GetArgsWithImportsHandling {

    @Test
    void shouldIncludeImportsInclude() {
      var strategy = new SlmeExtractStrategy(SlmeExtractMethod.BOT, List.of("GO:0008150"));
      var command = new RobotExtractCommand(strategy, null, HandlingImports.include, null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly("--method", "BOT", "--term", "GO:0008150", "--imports", "include");
    }

    @Test
    void shouldIncludeImportsExclude() {
      var strategy = new SlmeExtractStrategy(SlmeExtractMethod.BOT, List.of("GO:0008150"));
      var command = new RobotExtractCommand(strategy, null, HandlingImports.exclude, null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly("--method", "BOT", "--term", "GO:0008150", "--imports", "exclude");
    }
  }

  @Nested
  class GetArgsWithCopyOntologyAnnotations {

    @Test
    void shouldIncludeCopyOntologyAnnotationsTrue() {
      var strategy = new SlmeExtractStrategy(SlmeExtractMethod.BOT, List.of("GO:0008150"));
      var command = new RobotExtractCommand(strategy, null, null, true);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--method", "BOT", "--term", "GO:0008150", "--copy-ontology-annotations", "true");
    }

    @Test
    void shouldIncludeCopyOntologyAnnotationsFalse() {
      var strategy = new SlmeExtractStrategy(SlmeExtractMethod.BOT, List.of("GO:0008150"));
      var command = new RobotExtractCommand(strategy, null, null, false);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--method", "BOT", "--term", "GO:0008150", "--copy-ontology-annotations", "false");
    }
  }

  @Nested
  class GetArgsWithAllOptions {

    @Test
    void shouldIncludeAllOptionsInCorrectOrder() {
      var strategy = new SlmeExtractStrategy(SlmeExtractMethod.BOT, List.of("GO:0008150"));
      var command = new RobotExtractCommand(
          strategy,
          ExtractIntermediates.minimal,
          HandlingImports.include,
          true);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--method", "BOT",
              "--term", "GO:0008150",
              "--intermediates", "minimal",
              "--imports", "include",
              "--copy-ontology-annotations", "true");
    }

    @Test
    void shouldWorkWithMireotStrategy() {
      var strategy = new MireotExtractStrategy(
          List.of("GO:0008150"),
          List.of("GO:0009987"),
          List.of());
      var command = new RobotExtractCommand(
          strategy,
          ExtractIntermediates.all,
          HandlingImports.exclude,
          false);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--method", "MIREOT",
              "--upper-term", "GO:0008150",
              "--lower-term", "GO:0009987",
              "--intermediates", "all",
              "--imports", "exclude",
              "--copy-ontology-annotations", "false");
    }

    @Test
    void shouldWorkWithSubsetStrategy() {
      var strategy = new SubsetExtractStrategy(List.of("GO:0008150", "GO:0003674"));
      var command = new RobotExtractCommand(
          strategy,
          ExtractIntermediates.none,
          HandlingImports.include,
          true);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--method", "subset",
              "--term", "GO:0008150",
              "--term", "GO:0003674",
              "--intermediates", "none",
              "--imports", "include",
              "--copy-ontology-annotations", "true");
    }
  }

  @Nested
  class GetArgsWithPartialOptions {

    @Test
    void shouldIncludeOnlyIntermediatesAndCopyOntologyAnnotations() {
      var strategy = new SlmeExtractStrategy(SlmeExtractMethod.TOP, List.of("GO:0008150"));
      var command = new RobotExtractCommand(
          strategy,
          ExtractIntermediates.minimal,
          null,
          true);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--method", "TOP",
              "--term", "GO:0008150",
              "--intermediates", "minimal",
              "--copy-ontology-annotations", "true");
    }

    @Test
    void shouldIncludeOnlyImportsAndCopyOntologyAnnotations() {
      var strategy = new SlmeExtractStrategy(SlmeExtractMethod.STAR, List.of("GO:0008150"));
      var command = new RobotExtractCommand(
          strategy,
          null,
          HandlingImports.exclude,
          false);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--method", "STAR",
              "--term", "GO:0008150",
              "--imports", "exclude",
              "--copy-ontology-annotations", "false");
    }

    @Test
    void shouldIncludeOnlyIntermediatesAndImports() {
      var strategy = new SlmeExtractStrategy(SlmeExtractMethod.BOT, List.of("GO:0008150"));
      var command = new RobotExtractCommand(
          strategy,
          ExtractIntermediates.all,
          HandlingImports.include,
          null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--method", "BOT",
              "--term", "GO:0008150",
              "--intermediates", "all",
              "--imports", "include");
    }
  }

  @Nested
  class Immutability {

    @Test
    void shouldReturnImmutableList() {
      var strategy = new SlmeExtractStrategy(SlmeExtractMethod.BOT, List.of("GO:0008150"));
      var command = new RobotExtractCommand(
          strategy,
          ExtractIntermediates.minimal,
          HandlingImports.include,
          true);

      var args = command.getArgs();

      assertThat(args).isUnmodifiable();
    }
  }
}
