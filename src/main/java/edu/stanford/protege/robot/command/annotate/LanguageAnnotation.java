package edu.stanford.protege.robot.command.annotate;

import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * Annotation with language tag for internationalization.
 *
 * <p>
 * Maps to ROBOT's {@code --language-annotation} flag for language-tagged literals.
 *
 * <p>
 * Example: {@code new LanguageAnnotation("dc:title", "Exemple", "fr")} generates {@code
 * --language-annotation dc:title Exemple fr}
 *
 * @param property
 *          the annotation property IRI or prefixed name
 * @param value
 *          the annotation value
 * @param lang
 *          the ISO 639-1 language code (e.g., "en", "fr", "de")
 */
public record LanguageAnnotation(String property, String value, String lang) implements Annotation, HasLang {

  public static final String LANGUAGE_ANNOTATION = "--language-annotation";

  @Override
  public String getArgName() {
    return LANGUAGE_ANNOTATION;
  }

  @Override
  public List<String> getArgs() {
    return ImmutableList.of(getArgName(), property, value, lang);
  }
}
