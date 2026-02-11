package edu.stanford.protege.robot.service;

import static org.assertj.core.api.Assertions.assertThat;

import edu.stanford.protege.robot.command.annotate.AnnotateFlags;
import edu.stanford.protege.robot.command.annotate.LanguageAnnotation;
import edu.stanford.protege.robot.command.annotate.LinkAnnotation;
import edu.stanford.protege.robot.command.annotate.PlainAnnotation;
import edu.stanford.protege.robot.command.annotate.RobotAnnotateCommand;
import edu.stanford.protege.robot.command.annotate.TypedAnnotation;
import edu.stanford.protege.robot.command.collapse.RobotCollapseCommand;
import edu.stanford.protege.robot.command.convert.OboConvertStrategy;
import edu.stanford.protege.robot.command.convert.OwlConvertStrategy;
import edu.stanford.protege.robot.command.convert.RobotConvertCommand;
import edu.stanford.protege.robot.command.expand.ExpandFlags;
import edu.stanford.protege.robot.command.expand.RobotExpandCommand;
import edu.stanford.protege.robot.command.extract.ExtractFlags;
import edu.stanford.protege.robot.command.extract.ExtractIntermediates;
import edu.stanford.protege.robot.command.extract.HandlingImports;
import edu.stanford.protege.robot.command.extract.MireotExtractStrategy;
import edu.stanford.protege.robot.command.extract.RobotExtractCommand;
import edu.stanford.protege.robot.command.extract.SlmeExtractMethod;
import edu.stanford.protege.robot.command.extract.SlmeExtractStrategy;
import edu.stanford.protege.robot.command.extract.SubsetExtractStrategy;
import edu.stanford.protege.robot.service.config.JacksonConfiguration;
import edu.stanford.protege.robot.service.exception.RobotServiceException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;

/**
 * Tests for {@link RobotCommandParser} using example JSON files.
 *
 * <p>
 * This test class verifies JSON parsing, serialization, and round-trip conversion using the
 * comprehensive example JSON files in {@code src/test/resources/json-examples/}.
 */
@JsonTest
@Import({JacksonConfiguration.class, RobotCommandParser.class})
class RobotCommandParserTest {

    @Autowired
    private RobotCommandParser parser;

    /**
     * Tests parsing annotate command with all 4 annotation types.
     */
    @Test
    void testParseAnnotateCommand() throws IOException, RobotServiceException {
        // Load JSON from file
        var json = getJsonContent("/json-examples/annotate-command.json");

        // Parse single command
        var command = (RobotAnnotateCommand) parser.parseCommand(json);

        // Verify annotations (should have 9 total)
        assertThat(command.annotations()).hasSize(9);

        // Verify plain annotations
        var plainAnnotation1 = (PlainAnnotation) command.annotations().getFirst();
        assertThat(plainAnnotation1.property()).isEqualTo("rdfs:label");
        assertThat(plainAnnotation1.value()).isEqualTo("My Ontology");

        // Verify language annotations
        var langAnnotation1 = (LanguageAnnotation) command.annotations().get(2);
        assertThat(langAnnotation1.property()).isEqualTo("dc:title");
        assertThat(langAnnotation1.value()).isEqualTo("My Ontology");
        assertThat(langAnnotation1.lang()).isEqualTo("en");

        var langAnnotation2 = (LanguageAnnotation) command.annotations().get(3);
        assertThat(langAnnotation2.lang()).isEqualTo("fr");

        // Verify typed annotations
        var typedAnnotation1 = (TypedAnnotation) command.annotations().get(5);
        assertThat(typedAnnotation1.property()).isEqualTo("dc:date");
        assertThat(typedAnnotation1.value()).isEqualTo("2025-11-04");
        assertThat(typedAnnotation1.type()).isEqualTo("xsd:date");

        // Verify link annotations
        var linkAnnotation1 = (LinkAnnotation) command.annotations().get(7);
        assertThat(linkAnnotation1.property()).isEqualTo("dc:license");
        assertThat(linkAnnotation1.value())
                .isEqualTo("https://creativecommons.org/licenses/by/4.0/");

        // Verify flags
        assertThat(command.flags()).containsExactly(
                AnnotateFlags.INTERPOLATE,
                AnnotateFlags.ANNOTATE_DERIVED_FROM);
    }

    /**
     * Tests parsing expand command.
     */
    @Test
    void testParseExpandCommand() throws IOException, RobotServiceException {
        // Load JSON from file
        var json = getJsonContent("/json-examples/expand-command.json");

        // Parse single command
        var command = (RobotExpandCommand) parser.parseCommand(json);

        // Verify expand parameters
        assertThat(command.expandTerms()).containsExactly("UBERON:0000001", "UBERON:0000002");
        assertThat(command.noExpandTerms()).containsExactly("UBERON:9999999");
        assertThat(command.flags()).containsExactly(ExpandFlags.ANNOTATE_EXPANSION_AXIOMS);
    }

    /**
     * Tests parsing convert command with OBO format and cleaning options.
     */
    @Test
    void testParseConvertOboCommand() throws IOException, RobotServiceException {
        // Load JSON from file
        var json = getJsonContent("/json-examples/convert-obo-command.json");

        // Parse single command
        var command = (RobotConvertCommand) parser.parseCommand(json);

        // Verify convert convertStrategy is OboConvertStrategy
        assertThat(command.convertStrategy()).isInstanceOf(OboConvertStrategy.class);
        var oboStrategy = (OboConvertStrategy) command.convertStrategy();
        assertThat(oboStrategy.check()).isFalse();
        assertThat(oboStrategy.cleanOboOptions()).hasSize(2);
        assertThat(oboStrategy.addPrefixes())
                .hasSize(2)
                .containsEntry("CUSTOM", IRI.create("http://example.org/custom#"))
                .containsEntry("MY", IRI.create("http://example.org/my-ontology#"));
    }

    /**
     * Tests parsing convert command with OWL format.
     */
    @Test
    void testParseConvertOwlCommand() throws IOException, RobotServiceException {
        // Load JSON from file
        var json = getJsonContent("/json-examples/convert-owl-command.json");

        // Parse single command
        var command = (RobotConvertCommand) parser.parseCommand(json);

        // Verify convert convertStrategy is OwlConvertStrategy
        assertThat(command.convertStrategy()).isInstanceOf(OwlConvertStrategy.class);
    }

    /**
     * Tests parsing collapse command.
     */
    @Test
    void testParseCollapseCommand() throws IOException, RobotServiceException {
        // Load JSON from file
        var json = getJsonContent("/json-examples/collapse-command.json");

        // Parse single command
        var command = (RobotCollapseCommand) parser.parseCommand(json);

        // Verify collapse parameters
        assertThat(command.threshold()).isEqualTo(5);
        assertThat(command.preciousTerms()).containsExactly("GO:0008150", "GO:0003674", "GO:0005575");
    }

    /**
     * Tests parsing extract command with MIREOT strategy.
     */
    @Test
    void testParseExtractMireotCommand() throws IOException, RobotServiceException {
        // Load JSON from file
        var json = getJsonContent("/json-examples/extract-mireot-command.json");

        // Parse single command
        var command = (RobotExtractCommand) parser.parseCommand(json);

        // Verify extract command
        assertThat(command.extractStrategy()).isInstanceOf(MireotExtractStrategy.class);
        var mireotStrategy = (MireotExtractStrategy) command.extractStrategy();
        assertThat(mireotStrategy.upperTerms()).containsExactly("GO:0008150");
        assertThat(mireotStrategy.lowerTerms()).containsExactly("GO:0009987");
        assertThat(mireotStrategy.branchFromTerms()).containsExactly("GO:0008152");
        assertThat(command.extractIntermediates()).isEqualTo(ExtractIntermediates.all);
        assertThat(command.handlingImports()).isEqualTo(HandlingImports.exclude);
        assertThat(command.flags()).isEmpty();
    }

    /**
     * Tests parsing extract command with SLME strategy.
     */
    @Test
    void testParseExtractSlmeCommand() throws IOException, RobotServiceException {
        // Load JSON from file
        var json = getJsonContent("/json-examples/extract-slme-command.json");

        // Parse single command
        var command = (RobotExtractCommand) parser.parseCommand(json);

        // Verify extract command
        assertThat(command.extractStrategy()).isInstanceOf(SlmeExtractStrategy.class);
        var slmeStrategy = (SlmeExtractStrategy) command.extractStrategy();
        assertThat(slmeStrategy.method()).isEqualTo(SlmeExtractMethod.BOT);
        assertThat(slmeStrategy.terms()).containsExactly("GO:0008150", "GO:0003674", "GO:0005575");
        assertThat(command.extractIntermediates()).isEqualTo(ExtractIntermediates.minimal);
        assertThat(command.handlingImports()).isEqualTo(HandlingImports.include);
        assertThat(command.flags()).containsExactly(ExtractFlags.COPY_ONTOLOGY_ANNOTATIONS);
    }

    /**
     * Tests parsing extract command with subset strategy.
     */
    @Test
    void testParseExtractSubsetCommand() throws IOException, RobotServiceException {
        // Load JSON from file
        var json = getJsonContent("/json-examples/extract-subset-command.json");

        // Parse single command
        var command = (RobotExtractCommand) parser.parseCommand(json);

        // Verify extract command
        assertThat(command.extractStrategy()).isInstanceOf(SubsetExtractStrategy.class);
        var subsetStrategy = (SubsetExtractStrategy) command.extractStrategy();
        assertThat(subsetStrategy.terms()).containsExactly("GO:0008150", "GO:0003674", "GO:0005575",
                "GO:0008152", "GO:0009987");
        assertThat(command.extractIntermediates()).isEqualTo(ExtractIntermediates.none);
        assertThat(command.handlingImports()).isEqualTo(HandlingImports.include);
        assertThat(command.flags()).containsExactly(ExtractFlags.COPY_ONTOLOGY_ANNOTATIONS);
    }

    String getJsonContent(String path) throws IOException {
        return new String(Objects.requireNonNull(getClass().getResourceAsStream(path)).readAllBytes(),
                StandardCharsets.UTF_8);
    }
}
