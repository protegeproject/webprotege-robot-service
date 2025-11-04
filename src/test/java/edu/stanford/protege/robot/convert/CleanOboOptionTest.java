package edu.stanford.protege.robot.convert;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CleanOboOptionTest {

  @Test
  void dropExtraLabelsShouldReturnCorrectKeyword() {
    assertThat(CleanOboOption.drop_extra_labels.getKeyword()).isEqualTo("drop-extra-labels");
  }

  @Test
  void dropExtraDefinitionsShouldReturnCorrectKeyword() {
    assertThat(CleanOboOption.drop_extra_definitions.getKeyword()).isEqualTo("drop-extra-definitions");
  }

  @Test
  void dropExtraCommentsShouldReturnCorrectKeyword() {
    assertThat(CleanOboOption.drop_extra_comments.getKeyword()).isEqualTo("drop-extra-comments");
  }

  @Test
  void mergeCommentsShouldReturnCorrectKeyword() {
    assertThat(CleanOboOption.merge_comments.getKeyword()).isEqualTo("merge-comments");
  }

  @Test
  void dropUntranslatableAxiomsShouldReturnCorrectKeyword() {
    assertThat(CleanOboOption.drop_untranslatable_axioms.getKeyword()).isEqualTo("drop-untranslatable-axioms");
  }

  @Test
  void dropGciAxiomsShouldReturnCorrectKeyword() {
    assertThat(CleanOboOption.drop_gci_axioms.getKeyword()).isEqualTo("drop-gci-axioms");
  }

  @Test
  void strictShouldReturnCorrectKeyword() {
    assertThat(CleanOboOption.strict.getKeyword()).isEqualTo("strict");
  }

  @Test
  void simpleShouldReturnCorrectKeyword() {
    assertThat(CleanOboOption.simple.getKeyword()).isEqualTo("simple");
  }
}
