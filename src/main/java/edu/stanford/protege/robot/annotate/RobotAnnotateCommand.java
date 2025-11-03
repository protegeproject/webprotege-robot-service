package edu.stanford.protege.robot.annotate;

import com.google.common.collect.ImmutableList;
import edu.stanford.protege.RobotCommand;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import org.obolibrary.robot.AnnotateCommand;
import org.obolibrary.robot.Command;
import org.semanticweb.owlapi.model.IRI;

/**
 * ROBOT annotate command for adding metadata to ontologies.
 *
 * <p>
 * This command sets ontology/version IRIs and adds annotations like title, description, and
 * license. Supports plain, typed, language-tagged, and link annotations.
 *
 * <p>
 * Example:
 *
 * <pre>{@code
 * var command = new RobotAnnotateCommand(
 *     IRI.create("http://example.org/ontology"),
 *     IRI.create("http://example.org/ontology/v1.0"),
 *     List.of(
 *         new PlainAnnotation("rdfs:label", "Example Ontology"),
 *         new LanguageAnnotation("dc:title", "Exemple", "fr")),
 *     AnnotateFlags.INTERPOLATE);
 * }</pre>
 *
 * @param ontologyIri
 *          the ontology IRI to set (optional)
 * @param versionIri
 *          the version IRI to set (optional)
 * @param annotations
 *          list of annotations to add
 * @param flags
 *          optional behavior flags (INTERPOLATE, ANNOTATE_DERIVED_FROM, etc.)
 */
public record RobotAnnotateCommand(
    @Nullable IRI ontologyIri,
    @Nullable IRI versionIri,
    List<Annotation> annotations,
    AnnotateFlags... flags)
    implements
      RobotCommand {

  @Override
  public List<String> getArgs() {
    var args = ImmutableList.<String>builder();
    if (ontologyIri != null && !ontologyIri.isEmpty()) {
      args.add("--ontology-iri");
      args.add(ontologyIri.toString());
    }
    if (versionIri != null && !versionIri.isEmpty()) {
      args.add("--version-iri");
      args.add(versionIri.toString());
    }
    List<AnnotateFlags> flagsList = Arrays.asList(flags);
    if (flagsList.contains(AnnotateFlags.INTERPOLATE)) {
      args.add(AnnotateFlags.INTERPOLATE.getFlagName());
      args.add("true");
    }
    if (flagsList.contains(AnnotateFlags.REMOVE_ANNOTATIONS)) {
      args.add(AnnotateFlags.REMOVE_ANNOTATIONS.getFlagName());
      args.add("true");
    }
    if (flagsList.contains(AnnotateFlags.ANNOTATE_DERIVED_FROM)) {
      args.add(AnnotateFlags.ANNOTATE_DERIVED_FROM.getFlagName());
      args.add("true");
    }
    if (flagsList.contains(AnnotateFlags.ANNOTATE_DEFINED_BY)) {
      args.add(AnnotateFlags.ANNOTATE_DEFINED_BY.getFlagName());
      args.add("true");
    }
    annotations.forEach(
        annotation -> {
          args.addAll(annotation.getArgs());
        });
    return args.build();
  }

  @Override
  public Command getCommand() {
    return new AnnotateCommand();
  }
}
