package edu.stanford.protege.robot.command.convert;

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
      var command = new RobotConvertCommand(new JsonConvertStrategy());

      var result = command.getCommand();

      assertThat(result).isNotNull().isInstanceOf(ConvertCommand.class);
    }
  }

  @Nested
  class GetArgsWithFormat {

    @Test
    void shouldIncludeJsonFormat() {
      var command = new RobotConvertCommand(new JsonConvertStrategy());

      var args = command.getArgs();

      assertThat(args).containsExactly("--format", "json");
    }

    @Test
    void shouldIncludeOboFormat() {
      var command = new RobotConvertCommand(new OboConvertStrategy(null, null, null));

      var args = command.getArgs();

      assertThat(args).containsExactly("--format", "obo");
    }

    @Test
    void shouldIncludeOfnFormat() {
      var command = new RobotConvertCommand(new OfnConvertStrategy());

      var args = command.getArgs();

      assertThat(args).containsExactly("--format", "ofn");
    }

    @Test
    void shouldIncludeOmnFormat() {
      var command = new RobotConvertCommand(new OmnConvertStrategy());

      var args = command.getArgs();

      assertThat(args).containsExactly("--format", "omn");
    }

    @Test
    void shouldIncludeOwlFormat() {
      var command = new RobotConvertCommand(new OwlConvertStrategy());

      var args = command.getArgs();

      assertThat(args).containsExactly("--format", "owl");
    }

    @Test
    void shouldIncludeOwxFormat() {
      var command = new RobotConvertCommand(new OwxConvertStrategy());

      var args = command.getArgs();

      assertThat(args).containsExactly("--format", "owx");
    }

    @Test
    void shouldIncludeTtlFormat() {
      var command = new RobotConvertCommand(new TtlConvertStrategy());

      var args = command.getArgs();

      assertThat(args).containsExactly("--format", "ttl");
    }
  }

  @Nested
  class GetArgsWithCheck {

    @Test
    void shouldIncludeCheckTrue() {
      var strategy = new OboConvertStrategy(true, null, null);
      var command = new RobotConvertCommand(strategy);

      var args = command.getArgs();

      assertThat(args).containsExactly("--format", "obo", "--check", "true");
    }

    @Test
    void shouldIncludeCheckFalse() {
      var strategy = new OboConvertStrategy(false, null, null);
      var command = new RobotConvertCommand(strategy);

      var args = command.getArgs();

      assertThat(args).containsExactly("--format", "obo", "--check", "false");
    }
  }

  @Nested
  class GetArgsWithCleanOboOptions {

    @Test
    void shouldOmitCleanOboWhenListIsEmpty() {
      var strategy = new OboConvertStrategy(null, List.of(), null);
      var command = new RobotConvertCommand(strategy);

      var args = command.getArgs();

      assertThat(args).containsExactly("--format", "obo").doesNotContain("--clean-obo");
    }

    @Test
    void shouldIncludeSingleCleanOboOption() {
      var strategy = new OboConvertStrategy(null, List.of(CleanOboOption.drop_extra_labels), null);
      var command = new RobotConvertCommand(strategy);

      var args = command.getArgs();

      assertThat(args).containsExactly("--format", "obo", "--clean-obo", "drop-extra-labels");
    }

    @Test
    void shouldCombineMultipleCleanOboOptionsWithSpaces() {
      var strategy = new OboConvertStrategy(
          null,
          List.of(
              CleanOboOption.drop_extra_labels,
              CleanOboOption.drop_extra_definitions,
              CleanOboOption.merge_comments),
          null);
      var command = new RobotConvertCommand(strategy);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--format",
              "obo",
              "--clean-obo",
              "drop-extra-labels drop-extra-definitions merge-comments");
    }
  }

  @Nested
  class GetArgsWithAddPrefixes {

    @Test
    void shouldOmitAddPrefixWhenMapIsEmpty() {
      var strategy = new OboConvertStrategy(null, null, Map.of());
      var command = new RobotConvertCommand(strategy);

      var args = command.getArgs();

      assertThat(args).containsExactly("--format", "obo").doesNotContain("--add-prefix");
    }

    @Test
    void shouldIncludeSinglePrefix() {
      var strategy = new OboConvertStrategy(
          null, null, Map.of("CUSTOM", IRI.create("http://example.org/custom#")));
      var command = new RobotConvertCommand(strategy);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly("--format", "obo", "--add-prefix", "CUSTOM: http://example.org/custom#");
    }

    @Test
    void shouldIncludeMultiplePrefixes() {
      var strategy = new OboConvertStrategy(
          null,
          null,
          Map.of(
              "CUSTOM", IRI.create("http://example.org/custom#"),
              "FOO", IRI.create("http://example.org/foo#"),
              "BAR", IRI.create("http://example.org/bar#")));
      var command = new RobotConvertCommand(strategy);

      var args = command.getArgs();

      assertThat(args)
          .contains(
              "--format",
              "obo",
              "--add-prefix",
              "CUSTOM: http://example.org/custom#",
              "--add-prefix",
              "FOO: http://example.org/foo#",
              "--add-prefix",
              "BAR: http://example.org/bar#");
    }
  }

  @Nested
  class GetArgsWithCombinedParameters {

    @Test
    void shouldCombineFormatCheckAndCleanObo() {
      var strategy = new OboConvertStrategy(false, List.of(CleanOboOption.strict), null);
      var command = new RobotConvertCommand(strategy);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly("--format", "obo", "--check", "false", "--clean-obo", "strict");
    }

    @Test
    void shouldCombineCleanOboAndAddPrefix() {
      var strategy = new OboConvertStrategy(
          null,
          List.of(CleanOboOption.simple),
          Map.of("CUSTOM", IRI.create("http://example.org/custom#")));
      var command = new RobotConvertCommand(strategy);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--format",
              "obo",
              "--clean-obo",
              "simple",
              "--add-prefix",
              "CUSTOM: http://example.org/custom#");
    }

    @Test
    void shouldCombineAllParameters() {
      var strategy = new OboConvertStrategy(
          true,
          List.of(CleanOboOption.drop_extra_labels, CleanOboOption.merge_comments),
          Map.of(
              "CUSTOM",
              IRI.create("http://example.org/custom#"),
              "FOO",
              IRI.create("http://example.org/foo#")));
      var command = new RobotConvertCommand(strategy);

      var args = command.getArgs();

      assertThat(args)
          .contains(
              "--format",
              "obo",
              "--check",
              "true",
              "--clean-obo",
              "drop-extra-labels merge-comments",
              "--add-prefix",
              "CUSTOM: http://example.org/custom#",
              "--add-prefix",
              "FOO: http://example.org/foo#");
    }

    @Test
    void shouldHandleComplexOboConversion() {
      var strategy = new OboConvertStrategy(
          false,
          List.of(
              CleanOboOption.drop_extra_labels,
              CleanOboOption.drop_extra_definitions,
              CleanOboOption.drop_untranslatable_axioms),
          Map.of("EX", IRI.create("http://example.org#")));
      var command = new RobotConvertCommand(strategy);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--format",
              "obo",
              "--check",
              "false",
              "--clean-obo",
              "drop-extra-labels drop-extra-definitions drop-untranslatable-axioms",
              "--add-prefix",
              "EX: http://example.org#");
    }
  }
}
