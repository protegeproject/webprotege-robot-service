package edu.stanford.protege.robot.annotate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AnnotateFlagsTest {

  @Test
  void shouldReturnCorrectFlagNameForInterpolate() {
    assertThat(AnnotateFlags.INTERPOLATE.getFlagName()).isEqualTo("--interpolate");
  }

  @Test
  void shouldReturnCorrectFlagNameForAnnotateDerivedFrom() {
    assertThat(AnnotateFlags.ANNOTATE_DERIVED_FROM.getFlagName())
        .isEqualTo("--annotate-derived-from");
  }

  @Test
  void shouldReturnCorrectFlagNameForAnnotateDefinedBy() {
    assertThat(AnnotateFlags.ANNOTATE_DEFINED_BY.getFlagName())
        .isEqualTo("--annotate-defined-by");
  }

  @Test
  void shouldReturnCorrectFlagNameForRemoveAnnotations() {
    assertThat(AnnotateFlags.REMOVE_ANNOTATIONS.getFlagName())
        .isEqualTo("--remove-annotations");
  }
}
