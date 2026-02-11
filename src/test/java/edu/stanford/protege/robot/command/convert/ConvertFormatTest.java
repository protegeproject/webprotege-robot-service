package edu.stanford.protege.robot.command.convert;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ConvertFormatTest {

    @Test
    void shouldHaveJsonFormat() {
        assertThat(ConvertFormat.json.name()).isEqualTo("json");
    }

    @Test
    void shouldHaveOboFormat() {
        assertThat(ConvertFormat.obo.name()).isEqualTo("obo");
    }

    @Test
    void shouldHaveOfnFormat() {
        assertThat(ConvertFormat.ofn.name()).isEqualTo("ofn");
    }

    @Test
    void shouldHaveOmnFormat() {
        assertThat(ConvertFormat.omn.name()).isEqualTo("omn");
    }

    @Test
    void shouldHaveOwlFormat() {
        assertThat(ConvertFormat.owl.name()).isEqualTo("owl");
    }

    @Test
    void shouldHaveOwxFormat() {
        assertThat(ConvertFormat.owx.name()).isEqualTo("owx");
    }

    @Test
    void shouldHaveTtlFormat() {
        assertThat(ConvertFormat.ttl.name()).isEqualTo("ttl");
    }
}
