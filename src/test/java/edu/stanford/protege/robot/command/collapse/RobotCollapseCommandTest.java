package edu.stanford.protege.robot.command.collapse;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.obolibrary.robot.CollapseCommand;

class RobotCollapseCommandTest {

  @Nested
  class GetCommand {

    @Test
    void shouldReturnCollapseCommandInstance() {
      var command = new RobotCollapseCommand(null, List.of());

      var result = command.getCommand();

      assertThat(result).isNotNull().isInstanceOf(CollapseCommand.class);
    }
  }

  @Nested
  class GetArgsWithDefaultThreshold {

    @Test
    void shouldOmitThresholdWhenNull() {
      var command = new RobotCollapseCommand(null, List.of());

      var args = command.getArgs();

      assertThat(args).doesNotContain("--threshold");
    }

    @Test
    void shouldReturnEmptyArgsWhenNullThresholdAndEmptyPrecious() {
      var command = new RobotCollapseCommand(null, List.of());

      var args = command.getArgs();

      assertThat(args).isEmpty();
    }
  }

  @Nested
  class GetArgsWithCustomThreshold {

    @Test
    void shouldIncludeThreshold3() {
      var command = new RobotCollapseCommand(3, List.of());

      var args = command.getArgs();

      assertThat(args).containsExactly("--threshold", "3");
    }

    @Test
    void shouldIncludeThreshold10() {
      var command = new RobotCollapseCommand(10, List.of());

      var args = command.getArgs();

      assertThat(args).containsExactly("--threshold", "10");
    }

    @Test
    void shouldIncludeThreshold100() {
      var command = new RobotCollapseCommand(100, List.of());

      var args = command.getArgs();

      assertThat(args).containsExactly("--threshold", "100");
    }
  }

  @Nested
  class GetArgsWithPreciousTerms {

    @Test
    void shouldNotIncludePreciousWhenListIsEmpty() {
      var command = new RobotCollapseCommand(null, List.of());

      var args = command.getArgs();

      assertThat(args).doesNotContain("--precious");
    }

    @Test
    void shouldIncludeSinglePreciousTerm() {
      var command = new RobotCollapseCommand(null, List.of("GO:0008150"));

      var args = command.getArgs();

      assertThat(args).containsExactly("--precious", "GO:0008150");
    }

    @Test
    void shouldIncludeThreePreciousTerms() {
      var command = new RobotCollapseCommand(
          null, List.of("GO:0008150", "GO:0003674", "GO:0005575"));

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--precious", "GO:0008150",
              "--precious", "GO:0003674",
              "--precious", "GO:0005575");
    }

    @Test
    void shouldHandlePreciousTermWithFullIri() {
      var command = new RobotCollapseCommand(
          null, List.of("http://purl.obolibrary.org/obo/OBI_0000070"));

      var args = command.getArgs();

      assertThat(args)
          .containsExactly("--precious", "http://purl.obolibrary.org/obo/OBI_0000070");
    }

    @Test
    void shouldHandleMixedCuriesAndIris() {
      var command = new RobotCollapseCommand(
          null,
          List.of(
              "GO:0008150",
              "http://purl.obolibrary.org/obo/CHEBI_24431",
              "CHEBI:24431"));

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--precious", "GO:0008150",
              "--precious", "http://purl.obolibrary.org/obo/CHEBI_24431",
              "--precious", "CHEBI:24431");
    }
  }

  @Nested
  class GetArgsWithBothParameters {

    @Test
    void shouldIncludeThresholdAndSinglePreciousTerm() {
      var command = new RobotCollapseCommand(5, List.of("GO:0008150"));

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--threshold", "5",
              "--precious", "GO:0008150");
    }

    @Test
    void shouldIncludeThresholdAndMultiplePreciousTerms() {
      var command = new RobotCollapseCommand(3, List.of("GO:0008150", "GO:0003674"));

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--threshold", "3",
              "--precious", "GO:0008150",
              "--precious", "GO:0003674");
    }

    @Test
    void shouldIncludeThresholdAndThreePreciousTerms() {
      var command = new RobotCollapseCommand(
          10, List.of("GO:0008150", "GO:0003674", "GO:0005575"));

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--threshold", "10",
              "--precious", "GO:0008150",
              "--precious", "GO:0003674",
              "--precious", "GO:0005575");
    }
  }

  @Nested
  class GetArgsArray {

    @Test
    void shouldConvertArgsListToArray() {
      var command = new RobotCollapseCommand(5, List.of("GO:0008150"));

      var argsArray = command.getArgsArray();

      assertThat(argsArray)
          .isInstanceOf(String[].class)
          .containsExactly("--threshold", "5", "--precious", "GO:0008150");
    }

    @Test
    void shouldReturnEmptyArrayWhenNoArgs() {
      var command = new RobotCollapseCommand(null, List.of());

      var argsArray = command.getArgsArray();

      assertThat(argsArray).isEmpty();
    }
  }
}
