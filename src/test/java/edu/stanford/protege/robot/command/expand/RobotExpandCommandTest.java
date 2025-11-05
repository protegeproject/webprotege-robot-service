package edu.stanford.protege.robot.command.expand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import edu.stanford.protege.robot.command.expand.RobotExpandCommand;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.obolibrary.robot.ExpandCommand;

class RobotExpandCommandTest {

  @Nested
  class GetCommand {

    @Test
    void shouldReturnExpandCommandInstance() {
      var command = new RobotExpandCommand(List.of(), List.of(), null);

      var result = command.getCommand();

      assertThat(result).isNotNull().isInstanceOf(ExpandCommand.class);
    }
  }

  @Nested
  class GetArgsWithAnnotateExpansionAxioms {

    @Test
    void shouldOmitAnnotateExpansionAxiomsWhenNull() {
      var command = new RobotExpandCommand(List.of(), List.of(), null);

      var args = command.getArgs();

      assertThat(args).doesNotContain("--annotate-expansion-axioms");
    }

    @Test
    void shouldIncludeAnnotateExpansionAxiomsTrue() {
      var command = new RobotExpandCommand(List.of(), List.of(), true);

      var args = command.getArgs();

      assertThat(args).containsExactly("--annotate-expansion-axioms", "true");
    }

    @Test
    void shouldIncludeAnnotateExpansionAxiomsFalse() {
      var command = new RobotExpandCommand(List.of(), List.of(), false);

      var args = command.getArgs();

      assertThat(args).containsExactly("--annotate-expansion-axioms", "false");
    }
  }

  @Nested
  class GetArgsWithExpandTerms {

    @Test
    void shouldNotIncludeExpandTermWhenListIsEmpty() {
      var command = new RobotExpandCommand(List.of(), List.of(), null);

      var args = command.getArgs();

      assertThat(args).doesNotContain("--expand-term");
    }

    @Test
    void shouldIncludeSingleExpandTermWithCurie() {
      var command = new RobotExpandCommand(List.of("GO:0008150"), List.of(), null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--expand-term", "GO:0008150");
    }

    @Test
    void shouldIncludeSingleExpandTermWithFullIri() {
      var command = new RobotExpandCommand(
          List.of("http://purl.obolibrary.org/obo/GO_0003674"), List.of(), null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly("--expand-term", "http://purl.obolibrary.org/obo/GO_0003674");
    }

    @Test
    void shouldIncludeMultipleExpandTerms() {
      var command = new RobotExpandCommand(
          List.of("GO:0008150", "GO:0003674", "GO:0005575"), List.of(), null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--expand-term", "GO:0008150",
              "--expand-term", "GO:0003674",
              "--expand-term", "GO:0005575");
    }

    @Test
    void shouldHandleMixedCuriesAndIris() {
      var command = new RobotExpandCommand(
          List.of(
              "GO:0008150",
              "http://purl.obolibrary.org/obo/CHEBI_24431",
              "CHEBI:24431"),
          List.of(), null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--expand-term", "GO:0008150",
              "--expand-term", "http://purl.obolibrary.org/obo/CHEBI_24431",
              "--expand-term", "CHEBI:24431");
    }
  }

  @Nested
  class GetArgsWithNoExpandTerms {

    @Test
    void shouldNotIncludeNoExpandTermWhenListIsEmpty() {
      var command = new RobotExpandCommand(List.of(), List.of(), null);

      var args = command.getArgs();

      assertThat(args).doesNotContain("--no-expand-term");
    }

    @Test
    void shouldIncludeSingleNoExpandTermWithCurie() {
      var command = new RobotExpandCommand(List.of(), List.of("GO:0005575"), null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--no-expand-term", "GO:0005575");
    }

    @Test
    void shouldIncludeSingleNoExpandTermWithFullIri() {
      var command = new RobotExpandCommand(
          List.of(), List.of("http://purl.obolibrary.org/obo/OBI_0000070"), null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly("--no-expand-term", "http://purl.obolibrary.org/obo/OBI_0000070");
    }

    @Test
    void shouldIncludeMultipleNoExpandTerms() {
      var command = new RobotExpandCommand(
          List.of(), List.of("GO:0008150", "GO:0003674"), null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--no-expand-term", "GO:0008150",
              "--no-expand-term", "GO:0003674");
    }

    @Test
    void shouldHandleMixedCuriesAndIris() {
      var command = new RobotExpandCommand(
          List.of(), List.of(
              "GO:0005575",
              "http://purl.obolibrary.org/obo/BFO_0000001",
              "BFO:0000002"),
          null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--no-expand-term", "GO:0005575",
              "--no-expand-term", "http://purl.obolibrary.org/obo/BFO_0000001",
              "--no-expand-term", "BFO:0000002");
    }
  }

  @Nested
  class GetArgsWithCombinedParameters {

    @Test
    void shouldReturnEmptyArgsWhenAllParametersAreDefault() {
      var command = new RobotExpandCommand(List.of(), List.of(), null);

      var args = command.getArgs();

      assertThat(args).isEmpty();
    }

    @Test
    void shouldIncludeAnnotateExpansionAxiomsWhenTrue() {
      var command = new RobotExpandCommand(List.of(), List.of(), true);

      var args = command.getArgs();

      assertThat(args).containsExactly("--annotate-expansion-axioms", "true");
    }

    @Test
    void shouldIncludeAnnotateExpansionAxiomsWhenFalse() {
      var command = new RobotExpandCommand(List.of(), List.of(), false);

      var args = command.getArgs();

      assertThat(args).containsExactly("--annotate-expansion-axioms", "false");
    }

    @Test
    void shouldIncludeExpandTermsOnly() {
      var command = new RobotExpandCommand(
          List.of("GO:0008150", "GO:0003674"), List.of(), null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--expand-term", "GO:0008150",
              "--expand-term", "GO:0003674");
    }

    @Test
    void shouldCombineAnnotateWithNoExpandTerms() {
      var command = new RobotExpandCommand(
          List.of(), List.of("GO:0005575", "GO:0008150"), true);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--annotate-expansion-axioms", "true",
              "--no-expand-term", "GO:0005575",
              "--no-expand-term", "GO:0008150");
    }

    @Test
    void shouldCombineExpandTermsAndNoExpandTerms() {
      var command = new RobotExpandCommand(
          List.of("GO:0008150", "GO:0003674"), List.of("GO:0005575"), null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--expand-term", "GO:0008150",
              "--expand-term", "GO:0003674",
              "--no-expand-term", "GO:0005575");
    }

    @Test
    void shouldCombineAllParametersWithTrue() {
      var command = new RobotExpandCommand(
          List.of("GO:0008150"), List.of("GO:0005575"), true);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--annotate-expansion-axioms", "true",
              "--expand-term", "GO:0008150",
              "--no-expand-term", "GO:0005575");
    }

    @Test
    void shouldCombineAllParametersWithFalse() {
      var command = new RobotExpandCommand(
          List.of("GO:0008150", "GO:0003674"), List.of("GO:0005575", "BFO:0000001"), false);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--annotate-expansion-axioms", "false",
              "--expand-term", "GO:0008150",
              "--expand-term", "GO:0003674",
              "--no-expand-term", "GO:0005575",
              "--no-expand-term", "BFO:0000001");
    }

    @Test
    void shouldHandleComplexScenarioWithMixedTermFormats() {
      var command = new RobotExpandCommand(
          List.of(
              "GO:0008150",
              "http://purl.obolibrary.org/obo/GO_0003674"),
          List.of(
              "GO:0005575",
              "http://purl.obolibrary.org/obo/BFO_0000001",
              "CHEBI:24431"),
          false);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--annotate-expansion-axioms", "false",
              "--expand-term", "GO:0008150",
              "--expand-term", "http://purl.obolibrary.org/obo/GO_0003674",
              "--no-expand-term", "GO:0005575",
              "--no-expand-term", "http://purl.obolibrary.org/obo/BFO_0000001",
              "--no-expand-term", "CHEBI:24431");
    }
  }

  @Nested
  class GetArgsArray {

    @Test
    void shouldConvertArgsListToArray() {
      var command = new RobotExpandCommand(
          List.of("GO:0008150"), List.of("GO:0005575"), true);

      var argsArray = command.getArgsArray();

      assertThat(argsArray)
          .isInstanceOf(String[].class)
          .containsExactly(
              "--annotate-expansion-axioms", "true",
              "--expand-term", "GO:0008150",
              "--no-expand-term", "GO:0005575");
    }

    @Test
    void shouldReturnEmptyArrayWhenNoArgs() {
      var command = new RobotExpandCommand(List.of(), List.of(), null);

      var argsArray = command.getArgsArray();

      assertThat(argsArray).isEmpty();
    }

    @Test
    void shouldConvertArgsWithOnlyExpandTerms() {
      var command = new RobotExpandCommand(
          List.of("GO:0008150", "GO:0003674"), List.of(), null);

      var argsArray = command.getArgsArray();

      assertThat(argsArray)
          .containsExactly(
              "--expand-term", "GO:0008150",
              "--expand-term", "GO:0003674");
    }
  }
}
