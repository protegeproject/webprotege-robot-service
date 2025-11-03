package edu.stanford.protege.robot.extract;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class SubsetExtractStrategyTest {

  @Test
  void shouldGenerateCorrectArgsWithMultipleTerms() {
    var strategy = new SubsetExtractStrategy(
        List.of("GO:0008150", "GO:0003674", "GO:0005575"));

    var args = strategy.getArgs();

    assertThat(args)
        .containsExactly(
            "--method", "subset",
            "--term", "GO:0008150",
            "--term", "GO:0003674",
            "--term", "GO:0005575");
  }

  @Test
  void shouldGenerateCorrectArgsWithSingleTerm() {
    var strategy = new SubsetExtractStrategy(
        List.of("GO:0008150"));

    var args = strategy.getArgs();

    assertThat(args)
        .containsExactly(
            "--method", "subset",
            "--term", "GO:0008150");
  }

  @Test
  void shouldHandleEmptyTermList() {
    var strategy = new SubsetExtractStrategy(List.of());

    var args = strategy.getArgs();

    assertThat(args)
        .containsExactly("--method", "subset");
  }

  @Test
  void shouldHandleFullIris() {
    var strategy = new SubsetExtractStrategy(
        List.of("http://purl.obolibrary.org/obo/GO_0008150",
            "http://purl.obolibrary.org/obo/GO_0003674"));

    var args = strategy.getArgs();

    assertThat(args)
        .containsExactly(
            "--method", "subset",
            "--term", "http://purl.obolibrary.org/obo/GO_0008150",
            "--term", "http://purl.obolibrary.org/obo/GO_0003674");
  }

  @Test
  void shouldPreserveTermOrder() {
    var strategy = new SubsetExtractStrategy(
        List.of("GO:0001", "GO:0002", "GO:0003"));

    var args = strategy.getArgs();

    assertThat(args)
        .containsExactly(
            "--method", "subset",
            "--term", "GO:0001",
            "--term", "GO:0002",
            "--term", "GO:0003");
  }

  @Test
  void shouldReturnImmutableList() {
    var strategy = new SubsetExtractStrategy(
        List.of("GO:0008150"));

    var args = strategy.getArgs();

    assertThat(args).isUnmodifiable();
  }

  @Test
  void shouldHandleMixedCurieAndIriFormats() {
    var strategy = new SubsetExtractStrategy(
        List.of("GO:0008150", "http://purl.obolibrary.org/obo/GO_0003674"));

    var args = strategy.getArgs();

    assertThat(args)
        .containsExactly(
            "--method", "subset",
            "--term", "GO:0008150",
            "--term", "http://purl.obolibrary.org/obo/GO_0003674");
  }
}
