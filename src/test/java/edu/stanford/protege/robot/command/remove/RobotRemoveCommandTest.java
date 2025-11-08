package edu.stanford.protege.robot.command.remove;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.obolibrary.robot.RemoveCommand;

class RobotRemoveCommandTest {

  @Nested
  class GetCommand {

    @Test
    void shouldReturnRemoveCommandInstance() {
      var command = new RobotRemoveCommand(
          null,
          List.of("GO:0008150"),
          null,
          null,
          null,
          null,
          null);

      var result = command.getCommand();

      assertThat(result).isNotNull().isInstanceOf(RemoveCommand.class);
    }
  }

  @Nested
  class GetArgsWithTerms {

    @Test
    void shouldGenerateArgsForSingleTerm() {
      var command = new RobotRemoveCommand(
          null,
          List.of("GO:0008150"),
          null,
          null,
          null,
          null,
          null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--term", "GO:0008150");
    }

    @Test
    void shouldGenerateArgsForMultipleTerms() {
      var command = new RobotRemoveCommand(
          null,
          List.of("GO:0008150", "GO:0003674", "GO:0005575"),
          null,
          null,
          null,
          null,
          null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--term", "GO:0008150", "--term", "GO:0003674", "--term", "GO:0005575");
    }
  }

  @Nested
  class GetArgsWithExcludeAndIncludeTerms {

    @Test
    void shouldGenerateArgsForExcludeTerms() {
      var command = new RobotRemoveCommand(
          null,
          List.of("GO:0008150"),
          List.of("GO:0003674"),
          null,
          null,
          null,
          null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--term", "GO:0008150", "--exclude-term", "GO:0003674");
    }

    @Test
    void shouldGenerateArgsForIncludeTerms() {
      var command = new RobotRemoveCommand(
          null,
          List.of("GO:0008150"),
          null,
          List.of("GO:0005575"),
          null,
          null,
          null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--term", "GO:0008150", "--include-term", "GO:0005575");
    }

    @Test
    void shouldGenerateArgsForMultipleExcludeAndIncludeTerms() {
      var command = new RobotRemoveCommand(
          null,
          List.of("GO:0008150"),
          List.of("GO:0003674", "GO:0005623"),
          List.of("GO:0005575", "GO:0009987"),
          null,
          null,
          null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--term", "GO:0008150",
              "--exclude-term", "GO:0003674",
              "--exclude-term", "GO:0005623",
              "--include-term", "GO:0005575",
              "--include-term", "GO:0009987");
    }
  }

  @Nested
  class GetArgsWithSelectors {

    @Test
    void shouldGenerateArgsForSingleSelector() {
      var command = new RobotRemoveCommand(
          null,
          List.of("GO:0008150"),
          null,
          null,
          List.of("self"),
          null,
          null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--term", "GO:0008150", "--select", "self");
    }

    @Test
    void shouldGenerateArgsForMultipleSelectors() {
      var command = new RobotRemoveCommand(
          null,
          List.of("GO:0008150"),
          null,
          null,
          List.of("self", "descendants"),
          null,
          null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--term", "GO:0008150", "--select", "self", "--select", "descendants");
    }

    @Test
    void shouldSupportEnumSelectors() {
      var command = new RobotRemoveCommand(
          null,
          List.of("GO:0008150"),
          null,
          null,
          List.of(RemoveSelector.self.name(), RemoveSelector.descendants.name()),
          null,
          null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--term", "GO:0008150", "--select", "self", "--select", "descendants");
    }

    @Test
    void shouldSupportCustomPatternSelectors() {
      var command = new RobotRemoveCommand(
          null,
          null,
          null,
          null,
          List.of("owl:deprecated='true'^^xsd:boolean"),
          null,
          null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--select", "owl:deprecated='true'^^xsd:boolean");
    }

    @Test
    void shouldSupportIRIPatternSelectors() {
      var command = new RobotRemoveCommand(
          null,
          null,
          null,
          null,
          List.of("<http://purl.obolibrary.org/obo/BFO_*>"),
          null,
          null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--select", "<http://purl.obolibrary.org/obo/BFO_*>");
    }
  }

  @Nested
  class GetArgsWithAxiomTypes {

    @Test
    void shouldGenerateArgsForSingleAxiomType() {
      var command = new RobotRemoveCommand(
          null,
          List.of("GO:0008150"),
          null,
          null,
          null,
          List.of("subclass"),
          null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--term", "GO:0008150", "--axioms", "subclass");
    }

    @Test
    void shouldGenerateArgsForMultipleAxiomTypes() {
      var command = new RobotRemoveCommand(
          null,
          List.of("GO:0008150"),
          null,
          null,
          null,
          List.of("subclass", "equivalent"),
          null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--term", "GO:0008150", "--axioms", "subclass", "--axioms", "equivalent");
    }

    @Test
    void shouldSupportEnumAxiomTypes() {
      var command = new RobotRemoveCommand(
          null,
          null,
          null,
          null,
          null,
          List.of(RemoveAxiomType.structural_tautologies.name()),
          null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--axioms", "structural_tautologies");
    }

    @Test
    void shouldSupportAllAxiomTypesShortcut() {
      var command = new RobotRemoveCommand(
          null,
          List.of("GO:0008150"),
          null,
          null,
          null,
          List.of(RemoveAxiomType.all.name()),
          null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--term", "GO:0008150", "--axioms", "all");
    }
  }

  @Nested
  class GetArgsWithNonDefaultFlags {

    @Test
    void shouldIncludeSignatureFlag() {
      var command = new RobotRemoveCommand(
          null,
          List.of("GO:0008150"),
          null,
          null,
          null,
          null,
          null,
          RemoveFlags.SIGNATURE);

      var args = command.getArgs();

      assertThat(args).containsExactly("--term", "GO:0008150", "--signature", "true");
    }

    @Test
    void shouldIncludeNoTrimFlag() {
      var command = new RobotRemoveCommand(
          null,
          List.of("GO:0008150"),
          null,
          null,
          null,
          null,
          null,
          RemoveFlags.NO_TRIM);

      var args = command.getArgs();

      assertThat(args).containsExactly("--term", "GO:0008150", "--trim", "false");
    }

    @Test
    void shouldIncludeNoPreserveStructureFlag() {
      var command = new RobotRemoveCommand(
          null,
          List.of("GO:0008150"),
          null,
          null,
          null,
          null,
          null,
          RemoveFlags.NO_PRESERVE_STRUCTURE);

      var args = command.getArgs();

      assertThat(args).containsExactly("--term", "GO:0008150", "--preserve-structure", "false");
    }

    @Test
    void shouldIncludeAllowPunningFlag() {
      var command = new RobotRemoveCommand(
          null,
          List.of("GO:0008150"),
          null,
          null,
          null,
          null,
          null,
          RemoveFlags.ALLOW_PUNNING);

      var args = command.getArgs();

      assertThat(args).containsExactly("--term", "GO:0008150", "--allow-punning", "true");
    }

    @Test
    void shouldCombineMultipleNonDefaultFlags() {
      var command = new RobotRemoveCommand(
          null,
          List.of("GO:0008150"),
          null,
          null,
          null,
          null,
          null,
          RemoveFlags.SIGNATURE,
          RemoveFlags.NO_TRIM,
          RemoveFlags.NO_PRESERVE_STRUCTURE);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--term", "GO:0008150",
              "--signature", "true",
              "--trim", "false",
              "--preserve-structure", "false");
    }
  }

  @Nested
  class GetArgsWithBaseIri {

    @Test
    void shouldIncludeBaseIri() {
      var command = new RobotRemoveCommand(
          "http://purl.obolibrary.org/obo/",
          null,
          null,
          null,
          null,
          List.of("internal"),
          null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly("--base-iri", "http://purl.obolibrary.org/obo/", "--axioms", "internal");
    }

    @Test
    void shouldSupportExternalAxiomTypeWithBaseIri() {
      var command = new RobotRemoveCommand(
          "http://example.org/ontology#",
          null,
          null,
          null,
          null,
          List.of(RemoveAxiomType.external.name()),
          null);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly("--base-iri", "http://example.org/ontology#", "--axioms", "external");
    }
  }

  @Nested
  class GetArgsWithDropAxiomAnnotations {

    @Test
    void shouldIncludeSingleDropAxiomAnnotation() {
      var command = new RobotRemoveCommand(
          null,
          List.of("GO:0008150"),
          null,
          null,
          null,
          null,
          List.of("rdfs:comment"));

      var args = command.getArgs();

      assertThat(args)
          .containsExactly("--term", "GO:0008150", "--drop-axiom-annotations", "rdfs:comment");
    }

    @Test
    void shouldIncludeMultipleDropAxiomAnnotations() {
      var command = new RobotRemoveCommand(
          null,
          List.of("GO:0008150"),
          null,
          null,
          null,
          null,
          List.of("rdfs:comment", "dc:source"));

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--term", "GO:0008150",
              "--drop-axiom-annotations", "rdfs:comment",
              "--drop-axiom-annotations", "dc:source");
    }
  }

  @Nested
  class GetArgsWithComplexScenarios {

    @Test
    void shouldGenerateArgsForCompleteCommand() {
      var command = new RobotRemoveCommand(
          "http://purl.obolibrary.org/obo/", List.of("GO:0008150", "GO:0003674"),
          List.of("GO:0005575"),
          List.of("GO:0009987"),
          List.of("self", "descendants"),
          List.of("subclass", "equivalent"),
          List.of("rdfs:comment"),
          RemoveFlags.SIGNATURE,
          RemoveFlags.NO_TRIM,
          RemoveFlags.NO_PRESERVE_STRUCTURE,
          RemoveFlags.ALLOW_PUNNING);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--base-iri", "http://purl.obolibrary.org/obo/",
              "--term", "GO:0008150",
              "--term", "GO:0003674",
              "--exclude-term", "GO:0005575",
              "--include-term", "GO:0009987",
              "--select", "self",
              "--select", "descendants",
              "--axioms", "subclass",
              "--axioms", "equivalent",
              "--drop-axiom-annotations", "rdfs:comment",
              "--signature", "true",
              "--trim", "false",
              "--preserve-structure", "false",
              "--allow-punning", "true");
    }

    @Test
    void shouldHandleRemovingDeprecatedClassesExample() {
      var command = new RobotRemoveCommand(
          null,
          null,
          null,
          null,
          List.of("owl:deprecated='true'^^xsd:boolean"),
          null,
          null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--select", "owl:deprecated='true'^^xsd:boolean");
    }

    @Test
    void shouldHandleRemovingByNamespacePattern() {
      var command = new RobotRemoveCommand(
          null,
          null,
          null,
          null,
          List.of("<http://purl.obolibrary.org/obo/BFO_*>"),
          null,
          null,
          RemoveFlags.SIGNATURE);

      var args = command.getArgs();

      assertThat(args)
          .containsExactly(
              "--select", "<http://purl.obolibrary.org/obo/BFO_*>", "--signature", "true");
    }
  }

  @Nested
  class GetArgsWithNullOptions {

    @Test
    void shouldGenerateMinimalArgsWithOnlyTerms() {
      var command = new RobotRemoveCommand(null, List.of("GO:0008150"), null, null, null, null, null);

      var args = command.getArgs();

      assertThat(args).containsExactly("--term", "GO:0008150");
    }

    @Test
    void shouldHandleAllNullOptions() {
      var command = new RobotRemoveCommand(null, null, null, null, null, null, null);

      var args = command.getArgs();

      assertThat(args).isEmpty();
    }

    @Test
    void shouldIgnoreEmptyLists() {
      var command = new RobotRemoveCommand(
          null, List.of(), List.of(), List.of(), List.of(), List.of(), List.of());

      var args = command.getArgs();

      assertThat(args).isEmpty();
    }
  }

  @Nested
  class Immutability {

    @Test
    void shouldReturnImmutableList() {
      var command = new RobotRemoveCommand(
          null, List.of("GO:0008150"),
          null,
          null,
          List.of("self", "descendants"),
          List.of("subclass"),
          null,
          RemoveFlags.SIGNATURE,
          RemoveFlags.NO_TRIM,
          RemoveFlags.NO_PRESERVE_STRUCTURE);

      var args = command.getArgs();

      assertThat(args).isUnmodifiable();
    }
  }
}
