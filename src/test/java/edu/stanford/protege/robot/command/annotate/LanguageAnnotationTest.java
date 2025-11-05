package edu.stanford.protege.robot.command.annotate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LanguageAnnotationTest {

  @Test
  void shouldReturnCorrectArgName() {
    var annotation = new LanguageAnnotation("dc:title", "Title", "en");

    assertThat(annotation.getArgName()).isEqualTo("--language-annotation");
  }

  @Test
  void shouldReturnCorrectProperty() {
    var annotation = new LanguageAnnotation("rdfs:label", "Étiquette", "fr");

    assertThat(annotation.property()).isEqualTo("rdfs:label");
  }

  @Test
  void shouldReturnCorrectValue() {
    var annotation = new LanguageAnnotation("dc:description", "Beschreibung", "de");

    assertThat(annotation.value()).isEqualTo("Beschreibung");
  }

  @Test
  void shouldReturnCorrectLang() {
    var annotation = new LanguageAnnotation("dc:title", "Título", "es");

    assertThat(annotation.lang()).isEqualTo("es");
  }

  @Test
  void shouldGenerateCorrectArgs() {
    var annotation = new LanguageAnnotation("rdfs:comment", "Commentaire", "fr");

    assertThat(annotation.getArgs())
        .containsExactly("--language-annotation", "rdfs:comment", "Commentaire", "fr");
  }

  @Test
  void shouldHandleEnglishLanguage() {
    var annotation = new LanguageAnnotation("dc:title", "Example Ontology", "en");

    assertThat(annotation.getArgs())
        .containsExactly("--language-annotation", "dc:title", "Example Ontology", "en");
  }

  @Test
  void shouldHandleGermanLanguage() {
    var annotation = new LanguageAnnotation("rdfs:label", "Beispiel", "de");

    assertThat(annotation.getArgs())
        .containsExactly("--language-annotation", "rdfs:label", "Beispiel", "de");
  }

  @Test
  void shouldHandleJapaneseLanguage() {
    var annotation = new LanguageAnnotation("dc:title", "例", "ja");

    assertThat(annotation.getArgs())
        .containsExactly("--language-annotation", "dc:title", "例", "ja");
  }

  @Test
  void shouldImplementHasLangInterface() {
    var annotation = new LanguageAnnotation("dc:title", "Title", "en");

    assertThat(annotation).isInstanceOf(HasLang.class);
  }
}
