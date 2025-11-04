package edu.stanford.protege.robot.convert;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.obolibrary.robot.ConvertCommand;
import org.semanticweb.owlapi.model.IRI;

class RobotConvertCommandTest {

  @Nested
  class GetCommand {

    @Test
    void shouldReturnConvertCommandInstance() {
      var command = new RobotConvertCommand(null, null, null, null);

      var result = command.getCommand();

      assertThat(result).isNotNull().isInstanceOf(ConvertCommand.class);
    }
  }

  @Nested
  class GetArgsWithFormat {

    @Test
    void shouldIncludeJsonFormat() {
      var command = new RobotConvertCommand(ConvertFormat.json, null, null, null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--format", "json");
    }

    @Test
    void shouldIncludeOboFormat() {
      var command = new RobotConvertCommand(ConvertFormat.obo, null, null, null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--format", "obo");
    }

    @Test
    void shouldIncludeOfnFormat() {
      var command = new RobotConvertCommand(ConvertFormat.ofn, null, null, null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--format", "ofn");
    }

    @Test
    void shouldIncludeOmnFormat() {
      var command = new RobotConvertCommand(ConvertFormat.omn, null, null, null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--format", "omn");
    }

    @Test
    void shouldIncludeOwlFormat() {
      var command = new RobotConvertCommand(ConvertFormat.owl, null, null, null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--format", "owl");
    }

    @Test
    void shouldIncludeOwxFormat() {
      var command = new RobotConvertCommand(ConvertFormat.owx, null, null, null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--format", "owx");
    }

    @Test
    void shouldIncludeTtlFormat() {
      var command = new RobotConvertCommand(ConvertFormat.ttl, null, null, null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--format", "ttl");
    }
  }

  @Nested
  class GetArgsWithCheck {

    @Test
    void shouldIncludeCheckTrue() {
      var command = new RobotConvertCommand(null, true, null, null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--check", "true");
    }

    @Test
    void shouldIncludeCheckFalse() {
      var command = new RobotConvertCommand(null, false, null, null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--check", "false");
    }
  }

  @Nested
  class GetArgsWithCleanOboOptions {

    @Test
    void shouldOmitCleanOboWhenListIsEmpty() {
      var command = new RobotConvertCommand(null, null, List.of(), null);

      var args = command.getArgs();

      assertThat(args).doesNotContain("--clean-obo");
    }

    @Test
    void shouldIncludeSingleCleanOboOption() {
      var command = new RobotConvertCommand(
          null, null, List.of(CleanOboOption.drop_extra_labels), null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--clean-obo", "drop-extra-labels");
    }

    @Test
    void shouldCombineMultipleCleanOboOptionsWithSpaces() {
      var command = new RobotConvertCommand(
          null,
          null,
          List.of(
              CleanOboOption.drop_extra_labels,
              CleanOboOption.drop_extra_definitions,
              CleanOboOption.merge_comments),
          null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--clean-obo", "drop-extra-labels drop-extra-definitions merge-comments");
    }
  }

  @Nested
  class GetArgsWithAddPrefixes {

    @Test
    void shouldOmitAddPrefixWhenMapIsEmpty() {
      var command = new RobotConvertCommand(null, null, null, Map.of());

      var args = command.getArgs();

      assertThat(args).doesNotContain("--add-prefix");
    }

    @Test
    void shouldIncludeSinglePrefix() {
      var command = new RobotConvertCommand(
          null, null, null, Map.of("CUSTOM", IRI.create("http://example.org/custom#")));

      var args = command.getArgs();

      assertThat(args).containsExactly("--add-prefix", "CUSTOM: http://example.org/custom#");
    }

    @Test
    void shouldIncludeMultiplePrefixes() {
      var command = new RobotConvertCommand(
          null,
          null,
          null,
          Map.of(
              "CUSTOM", IRI.create("http://example.org/custom#"),
              "FOO", IRI.create("http://example.org/foo#"),
              "BAR", IRI.create("http://example.org/bar#")));

      var args = command.getArgs();

      assertThat(args)
          .contains(
              "--add-prefix", "CUSTOM: http://example.org/custom#",
              "--add-prefix", "FOO: http://example.org/foo#",
              "--add-prefix", "BAR: http://example.org/bar#");
    }
  }

  @Nested
  class GetArgsWithCombinedParameters {

    @Test
    void shouldCombineFormatCheckAndCleanObo() {
      var command = new RobotConvertCommand(
          ConvertFormat.obo, false, List.of(CleanOboOption.strict), null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--format", "obo", "--check", "false", "--clean-obo",
          "strict");
    }

    @Test
    void shouldCombineCleanOboAndAddPrefix() {
      var command = new RobotConvertCommand(
          null,
          null,
          List.of(CleanOboOption.simple),
          Map.of("CUSTOM", IRI.create("http://example.org/custom#")));

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--clean-obo", "simple",
              "--add-prefix", "CUSTOM: http://example.org/custom#");
    }

    @Test
    void shouldCombineAllParameters() {
      var command = new RobotConvertCommand(
          ConvertFormat.obo,
          true,
          List.of(CleanOboOption.drop_extra_labels, CleanOboOption.merge_comments),
          Map.of("CUSTOM", IRI.create("http://example.org/custom#"), "FOO", IRI.create("http://example.org/foo#")));

      var args = command.getArgs();

      assertThat(args)
          .contains(
              "--format", "obo",
              "--check", "true",
              "--clean-obo", "drop-extra-labels merge-comments",
              "--add-prefix", "CUSTOM: http://example.org/custom#",
              "--add-prefix", "FOO: http://example.org/foo#");
    }

    @Test
    void shouldHandleComplexOboConversion() {
      var command = new RobotConvertCommand(
          ConvertFormat.obo,
          false,
          List.of(
              CleanOboOption.drop_extra_labels,
              CleanOboOption.drop_extra_definitions,
              CleanOboOption.drop_untranslatable_axioms),
          Map.of("EX", IRI.create("http://example.org#")));

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--format", "obo",
              "--check", "false",
              "--clean-obo",
              "drop-extra-labels drop-extra-definitions drop-untranslatable-axioms",
              "--add-prefix", "EX: http://example.org#");
    }
  }
}
