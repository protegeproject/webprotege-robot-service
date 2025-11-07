package edu.stanford.protege.robot.service.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.robot.command.RobotCommand;
import edu.stanford.protege.robot.command.annotate.AnnotateFlags;
import edu.stanford.protege.robot.command.annotate.PlainAnnotation;
import edu.stanford.protege.robot.command.annotate.RobotAnnotateCommand;
import edu.stanford.protege.robot.command.collapse.RobotCollapseCommand;
import edu.stanford.protege.robot.command.convert.JsonConvertStrategy;
import edu.stanford.protege.robot.command.convert.OwlConvertStrategy;
import edu.stanford.protege.robot.command.convert.RobotConvertCommand;
import edu.stanford.protege.robot.command.expand.RobotExpandCommand;
import edu.stanford.protege.robot.command.extract.ExtractIntermediates;
import edu.stanford.protege.robot.command.extract.HandlingImports;
import edu.stanford.protege.robot.command.extract.RobotExtractCommand;
import edu.stanford.protege.robot.command.extract.SlmeExtractMethod;
import edu.stanford.protege.robot.command.extract.SlmeExtractStrategy;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;

/**
 * Unit tests for RobotCommand polymorphic JSON serialization and deserialization.
 *
 * <p>
 * Tests verify that all RobotCommand implementations can be correctly serialized to JSON with type
 * discriminators and deserialized back to the correct concrete classes. This includes testing
 * nested polymorphic types (Annotation, ExtractStrategy) and custom types (IRI).
 */
@JsonTest
@Import(JacksonConfiguration.class)
class RobotCommandMixinTest {

  @Autowired
  private ObjectMapper objectMapper;

  /**
   * Tests RobotAnnotateCommand serialization with type discriminator.
   */
  @Test
  void testAnnotateCommandSerialization() throws Exception {
    RobotAnnotateCommand command = new RobotAnnotateCommand(IRI.create("http://example.org/ont"),
        IRI.create("http://example.org/ont/v1.0"),
        List.of(new PlainAnnotation("rdfs:label", "Example Ontology")),
        AnnotateFlags.INTERPOLATE);
    String json = objectMapper.writeValueAsString(command);

    assertNotNull(json);
    assertTrue(json.contains("\"@type\":\"AnnotateCommand\""));
    assertTrue(json.contains("\"ontologyIri\":\"http://example.org/ont\""));
    assertTrue(json.contains("\"versionIri\":\"http://example.org/ont/v1.0\""));
    assertTrue(json.contains("\"rdfs:label\""));
  }

  /**
   * Tests RobotAnnotateCommand deserialization from JSON.
   */
  @Test
  void testAnnotateCommandDeserialization() throws Exception {
    String json = "{"
        + "\"@type\":\"AnnotateCommand\","
        + "\"ontologyIri\":\"http://example.org/ont\","
        + "\"versionIri\":\"http://example.org/ont/v1.0\","
        + "\"annotations\":["
        + "{\"@type\":\"PlainAnnotation\",\"property\":\"rdfs:label\",\"value\":\"Test\"}"
        + "],"
        + "\"flags\":[\"INTERPOLATE\"]"
        + "}";
    RobotCommand command = objectMapper.readValue(json, RobotCommand.class);

    assertNotNull(command);
    assertInstanceOf(RobotAnnotateCommand.class, command);
    RobotAnnotateCommand annotateCmd = (RobotAnnotateCommand) command;
    assertEquals(IRI.create("http://example.org/ont"), annotateCmd.ontologyIri());
    assertEquals(IRI.create("http://example.org/ont/v1.0"), annotateCmd.versionIri());
    assertEquals(1, annotateCmd.annotations().size());
  }

  /**
   * Tests RobotExtractCommand serialization with nested ExtractStrategy polymorphism.
   */
  @Test
  void testExtractCommandSerialization() throws Exception {
    SlmeExtractStrategy strategy = new SlmeExtractStrategy(SlmeExtractMethod.BOT, List.of("GO:0008150"));
    RobotExtractCommand command = new RobotExtractCommand(strategy, ExtractIntermediates.minimal,
        HandlingImports.include, true);
    String json = objectMapper.writeValueAsString(command);

    assertNotNull(json);
    assertTrue(json.contains("\"@type\":\"ExtractCommand\""));
    assertTrue(json.contains("\"extractStrategy\""));
    assertTrue(json.contains("\"@type\":\"SLME\""));
    assertTrue(json.contains("\"method\":\"BOT\""));
    assertTrue(json.contains("\"extractIntermediates\":\"minimal\""));
  }

  /**
   * Tests RobotExtractCommand deserialization from JSON.
   */
  @Test
  void testExtractCommandDeserialization() throws Exception {
    String json = "{"
        + "\"@type\":\"ExtractCommand\","
        + "\"extractStrategy\":{"
        + "\"@type\":\"SLME\","
        + "\"method\":\"BOT\","
        + "\"terms\":[\"GO:0008150\"]"
        + "},"
        + "\"extractIntermediates\":\"minimal\","
        + "\"handlingImports\":\"include\","
        + "\"copyOntologyAnnotations\":true"
        + "}";
    RobotCommand command = objectMapper.readValue(json, RobotCommand.class);

    assertNotNull(command);
    assertInstanceOf(RobotExtractCommand.class, command);
    RobotExtractCommand extractCmd = (RobotExtractCommand) command;
    assertInstanceOf(SlmeExtractStrategy.class, extractCmd.extractStrategy());
    assertEquals(ExtractIntermediates.minimal, extractCmd.extractIntermediates());
  }

  /**
   * Tests RobotCollapseCommand serialization.
   */
  @Test
  void testCollapseCommandSerialization() throws Exception {
    RobotCollapseCommand command = new RobotCollapseCommand(5, List.of("GO:0008150", "GO:0003674"));
    String json = objectMapper.writeValueAsString(command);

    assertNotNull(json);
    assertTrue(json.contains("\"@type\":\"CollapseCommand\""));
    assertTrue(json.contains("\"threshold\":5"));
    assertTrue(json.contains("\"preciousTerms\""));
    assertTrue(json.contains("GO:0008150"));
  }

  /**
   * Tests RobotCollapseCommand deserialization from JSON.
   */
  @Test
  void testCollapseCommandDeserialization() throws Exception {
    String json = "{"
        + "\"@type\":\"CollapseCommand\","
        + "\"threshold\":5,"
        + "\"preciousTerms\":[\"GO:0008150\",\"GO:0003674\"]"
        + "}";
    RobotCommand command = objectMapper.readValue(json, RobotCommand.class);

    assertNotNull(command);
    assertInstanceOf(RobotCollapseCommand.class, command);
    RobotCollapseCommand collapseCmd = (RobotCollapseCommand) command;
    assertEquals(5, collapseCmd.threshold());
    assertEquals(2, collapseCmd.preciousTerms().size());
  }

  /**
   * Tests RobotConvertCommand serialization.
   */
  @Test
  void testConvertCommandSerialization() throws Exception {
    RobotConvertCommand command = new RobotConvertCommand(new JsonConvertStrategy());
    String json = objectMapper.writeValueAsString(command);

    assertNotNull(json);
    assertTrue(json.contains("\"@type\":\"ConvertCommand\""));
    assertTrue(json.contains("\"convertStrategy\""));
  }

  /**
   * Tests RobotConvertCommand deserialization from JSON.
   */
  @Test
  void testConvertCommandDeserialization() throws Exception {
    String json = "{"
        + "\"@type\":\"ConvertCommand\","
        + "\"convertStrategy\":{\"@type\":\"JSON\"}"
        + "}";
    RobotCommand command = objectMapper.readValue(json, RobotCommand.class);

    assertNotNull(command);
    assertInstanceOf(RobotConvertCommand.class, command);
    RobotConvertCommand convertCmd = (RobotConvertCommand) command;
    assertInstanceOf(JsonConvertStrategy.class, convertCmd.convertStrategy());
  }

  /**
   * Tests RobotExpandCommand serialization.
   */
  @Test
  void testExpandCommandSerialization() throws Exception {
    RobotExpandCommand command = new RobotExpandCommand(List.of("UBERON:0000001"), List.of(), true);
    String json = objectMapper.writeValueAsString(command);

    assertNotNull(json);
    assertTrue(json.contains("\"@type\":\"ExpandCommand\""));
    assertTrue(json.contains("\"expandTerms\""));
    assertTrue(json.contains("UBERON:0000001"));
    assertTrue(json.contains("\"annotateExpansionAxioms\":true"));
  }

  /**
   * Tests RobotExpandCommand deserialization from JSON.
   */
  @Test
  void testExpandCommandDeserialization() throws Exception {
    String json = "{"
        + "\"@type\":\"ExpandCommand\","
        + "\"expandTerms\":[\"UBERON:0000001\"],"
        + "\"noExpandTerms\":[],"
        + "\"annotateExpansionAxioms\":true"
        + "}";
    RobotCommand command = objectMapper.readValue(json, RobotCommand.class);

    assertNotNull(command);
    assertInstanceOf(RobotExpandCommand.class, command);
    RobotExpandCommand expandCmd = (RobotExpandCommand) command;
    assertEquals(1, expandCmd.expandTerms().size());
    assertEquals("UBERON:0000001", expandCmd.expandTerms().get(0));
    assertEquals(true, expandCmd.annotateExpansionAxioms());
  }

  /**
   * Tests round-trip serialization for all command types.
   */
  @Test
  void testCommandRoundTrip() throws Exception {
    RobotCommand[] commands = {
        new RobotAnnotateCommand(IRI.create("http://example.org/ont"), null,
            List.of(new PlainAnnotation("rdfs:label", "Test"))),
        new RobotExtractCommand(
            new SlmeExtractStrategy(SlmeExtractMethod.BOT, List.of("GO:0008150")), null, null,
            null),
        new RobotCollapseCommand(5, List.of("GO:0008150")),
        new RobotConvertCommand(new OwlConvertStrategy()),
        new RobotExpandCommand(List.of(), List.of(), null)};

    for (RobotCommand original : commands) {
      String json = objectMapper.writeValueAsString(original);
      RobotCommand deserialized = objectMapper.readValue(json, RobotCommand.class);

      assertNotNull(deserialized);
      assertEquals(original.getClass(), deserialized.getClass());
    }
  }
}
