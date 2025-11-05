package edu.stanford.protege.robot.command.extract;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class SlmeExtractStrategyTest {

  @Test
  void shouldGenerateCorrectArgsWithBotMethod() {
    var strategy = new SlmeExtractStrategy(
        SlmeExtractMethod.BOT,
        List.of("GO:0008150", "GO:0003674"));

    var args = strategy.getArgs();

    assertThat(args)
        .containsExactly(
            "--method", "BOT",
            "--term", "GO:0008150",
            "--term", "GO:0003674");
  }

  @Test
  void shouldGenerateCorrectArgsWithTopMethod() {
    var strategy = new SlmeExtractStrategy(
        SlmeExtractMethod.TOP,
        List.of("GO:0008150"));

    var args = strategy.getArgs();

    assertThat(args)
        .containsExactly(
            "--method", "TOP",
            "--term", "GO:0008150");
  }

  @Test
  void shouldGenerateCorrectArgsWithStarMethod() {
    var strategy = new SlmeExtractStrategy(
        SlmeExtractMethod.STAR,
        List.of("GO:0008150", "GO:0003674", "GO:0005575"));

    var args = strategy.getArgs();

    assertThat(args)
        .containsExactly(
            "--method", "STAR",
            "--term", "GO:0008150",
            "--term", "GO:0003674",
            "--term", "GO:0005575");
  }

  @Test
  void shouldHandleSingleTerm() {
    var strategy = new SlmeExtractStrategy(
        SlmeExtractMethod.BOT,
        List.of("GO:0008150"));

    var args = strategy.getArgs();

    assertThat(args)
        .containsExactly(
            "--method", "BOT",
            "--term", "GO:0008150");
  }

  @Test
  void shouldHandleEmptyTermList() {
    var strategy = new SlmeExtractStrategy(
        SlmeExtractMethod.BOT,
        List.of());

    var args = strategy.getArgs();

    assertThat(args)
        .containsExactly("--method", "BOT");
  }

  @Test
  void shouldHandleFullIris() {
    var strategy = new SlmeExtractStrategy(
        SlmeExtractMethod.BOT,
        List.of("http://purl.obolibrary.org/obo/GO_0008150"));

    var args = strategy.getArgs();

    assertThat(args)
        .containsExactly(
            "--method", "BOT",
            "--term", "http://purl.obolibrary.org/obo/GO_0008150");
  }

  @Test
  void shouldPreserveTermOrder() {
    var strategy = new SlmeExtractStrategy(
        SlmeExtractMethod.BOT,
        List.of("GO:0001", "GO:0002", "GO:0003"));

    var args = strategy.getArgs();

    assertThat(args)
        .containsExactly(
            "--method", "BOT",
            "--term", "GO:0001",
            "--term", "GO:0002",
            "--term", "GO:0003");
  }

  @Test
  void shouldReturnImmutableList() {
    var strategy = new SlmeExtractStrategy(
        SlmeExtractMethod.BOT,
        List.of("GO:0008150"));

    var args = strategy.getArgs();

    assertThat(args).isUnmodifiable();
  }

  @Test
  void shouldHandleMultipleTermsWithAllMethods() {
    var terms = List.of("GO:0001", "GO:0002", "GO:0003");

    for (var method : SlmeExtractMethod.values()) {
      var strategy = new SlmeExtractStrategy(method, terms);
      var args = strategy.getArgs();

      assertThat(args)
          .as("Method %s should include all terms", method)
          .contains("--method", method.name())
          .contains("--term", "GO:0001")
          .contains("--term", "GO:0002")
          .contains("--term", "GO:0003");
    }
  }
}
