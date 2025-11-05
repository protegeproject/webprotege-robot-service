package edu.stanford.protege.robot.command.extract;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class MireotExtractStrategyTest {

  @Test
  void shouldGenerateCorrectArgsWithAllTerms() {
    var strategy = new MireotExtractStrategy(
        List.of("GO:0008150", "GO:0003674"),
        List.of("GO:0009987", "GO:0008152"),
        List.of("GO:0005575"));

    var args = strategy.getArgs();

    assertThat(args)
        .containsExactly(
            "--method", "MIREOT",
            "--upper-term", "GO:0008150",
            "--upper-term", "GO:0003674",
            "--lower-term", "GO:0009987",
            "--lower-term", "GO:0008152",
            "--branch-from-term", "GO:0005575");
  }

  @Test
  void shouldGenerateArgsWithOnlyLowerTerms() {
    var strategy = new MireotExtractStrategy(
        List.of(),
        List.of("GO:0009987"),
        List.of());

    var args = strategy.getArgs();

    assertThat(args)
        .containsExactly(
            "--method", "MIREOT",
            "--lower-term", "GO:0009987");
  }

  @Test
  void shouldGenerateArgsWithOnlyUpperTerms() {
    var strategy = new MireotExtractStrategy(
        List.of("GO:0008150"),
        List.of(),
        List.of());

    var args = strategy.getArgs();

    assertThat(args)
        .containsExactly(
            "--method", "MIREOT",
            "--upper-term", "GO:0008150");
  }

  @Test
  void shouldGenerateArgsWithOnlyBranchFromTerms() {
    var strategy = new MireotExtractStrategy(
        List.of(),
        List.of(),
        List.of("GO:0005575", "GO:0008150"));

    var args = strategy.getArgs();

    assertThat(args)
        .containsExactly(
            "--method", "MIREOT",
            "--branch-from-term", "GO:0005575",
            "--branch-from-term", "GO:0008150");
  }

  @Test
  void shouldGenerateMinimalArgsWithEmptyLists() {
    var strategy = new MireotExtractStrategy(
        List.of(),
        List.of(),
        List.of());

    var args = strategy.getArgs();

    assertThat(args)
        .containsExactly("--method", "MIREOT");
  }

  @Test
  void shouldHandleFullIris() {
    var strategy = new MireotExtractStrategy(
        List.of("http://purl.obolibrary.org/obo/GO_0008150"),
        List.of("http://purl.obolibrary.org/obo/GO_0009987"),
        List.of());

    var args = strategy.getArgs();

    assertThat(args)
        .containsExactly(
            "--method", "MIREOT",
            "--upper-term", "http://purl.obolibrary.org/obo/GO_0008150",
            "--lower-term", "http://purl.obolibrary.org/obo/GO_0009987");
  }

  @Test
  void shouldPreserveTermOrder() {
    var strategy = new MireotExtractStrategy(
        List.of("GO:0001", "GO:0002", "GO:0003"),
        List.of(),
        List.of());

    var args = strategy.getArgs();

    assertThat(args)
        .containsExactly(
            "--method", "MIREOT",
            "--upper-term", "GO:0001",
            "--upper-term", "GO:0002",
            "--upper-term", "GO:0003");
  }

  @Test
  void shouldReturnImmutableList() {
    var strategy = new MireotExtractStrategy(
        List.of("GO:0008150"),
        List.of("GO:0009987"),
        List.of());

    var args = strategy.getArgs();

    assertThat(args).isUnmodifiable();
  }
}
