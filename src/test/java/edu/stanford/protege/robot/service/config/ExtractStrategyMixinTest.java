package edu.stanford.protege.robot.service.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.robot.command.extract.ExtractStrategy;
import edu.stanford.protege.robot.command.extract.MireotExtractStrategy;
import edu.stanford.protege.robot.command.extract.SlmeExtractMethod;
import edu.stanford.protege.robot.command.extract.SlmeExtractStrategy;
import edu.stanford.protege.robot.command.extract.SubsetExtractStrategy;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;

/**
 * Unit tests for ExtractStrategy polymorphic JSON serialization and deserialization.
 *
 * <p>
 * Tests verify that all ExtractStrategy implementations can be correctly serialized to JSON with
 * type discriminators and deserialized back to the correct concrete classes.
 */
@JsonTest
@Import(JacksonConfiguration.class)
class ExtractStrategyMixinTest {

  @Autowired
  private ObjectMapper objectMapper;

  /**
   * Tests SlmeExtractStrategy serialization with type discriminator.
   */
  @Test
  void testSlmeExtractStrategySerialization() throws Exception {
    SlmeExtractStrategy strategy = new SlmeExtractStrategy(SlmeExtractMethod.BOT, List.of("GO:0008150", "GO:0003674"));
    String json = objectMapper.writeValueAsString(strategy);

    assertNotNull(json);
    assertTrue(json.contains("\"@type\":\"SLME\""));
    assertTrue(json.contains("\"method\":\"BOT\""));
    assertTrue(json.contains("\"terms\""));
    assertTrue(json.contains("GO:0008150"));
    assertTrue(json.contains("GO:0003674"));
  }

  /**
   * Tests SlmeExtractStrategy deserialization from JSON with type discriminator.
   */
  @Test
  void testSlmeExtractStrategyDeserialization() throws Exception {
    String json = "{\"@type\":\"SLME\",\"method\":\"BOT\",\"terms\":[\"GO:0008150\",\"GO:0003674\"]}";
    ExtractStrategy strategy = objectMapper.readValue(json, ExtractStrategy.class);

    assertNotNull(strategy);
    assertInstanceOf(SlmeExtractStrategy.class, strategy);
    SlmeExtractStrategy slme = (SlmeExtractStrategy) strategy;
    assertEquals(SlmeExtractMethod.BOT, slme.method());
    assertEquals(2, slme.terms().size());
    assertEquals("GO:0008150", slme.terms().get(0));
    assertEquals("GO:0003674", slme.terms().get(1));
  }

  /**
   * Tests SlmeExtractStrategy with STAR method.
   */
  @Test
  void testSlmeExtractStrategyStarMethod() throws Exception {
    String json = "{\"@type\":\"SLME\",\"method\":\"STAR\",\"terms\":[\"GO:0008150\"]}";
    ExtractStrategy strategy = objectMapper.readValue(json, ExtractStrategy.class);

    assertNotNull(strategy);
    assertInstanceOf(SlmeExtractStrategy.class, strategy);
    SlmeExtractStrategy slme = (SlmeExtractStrategy) strategy;
    assertEquals(SlmeExtractMethod.STAR, slme.method());
  }

  /**
   * Tests SlmeExtractStrategy with TOP method.
   */
  @Test
  void testSlmeExtractStrategyTopMethod() throws Exception {
    String json = "{\"@type\":\"SLME\",\"method\":\"TOP\",\"terms\":[\"GO:0008150\"]}";
    ExtractStrategy strategy = objectMapper.readValue(json, ExtractStrategy.class);

    assertNotNull(strategy);
    assertInstanceOf(SlmeExtractStrategy.class, strategy);
    SlmeExtractStrategy slme = (SlmeExtractStrategy) strategy;
    assertEquals(SlmeExtractMethod.TOP, slme.method());
  }

  /**
   * Tests MireotExtractStrategy serialization with type discriminator.
   */
  @Test
  void testMireotExtractStrategySerialization() throws Exception {
    MireotExtractStrategy strategy = new MireotExtractStrategy(List.of("GO:0008150"),
        List.of("GO:0009987"), List.of("GO:0008152"));
    String json = objectMapper.writeValueAsString(strategy);

    assertNotNull(json);
    assertTrue(json.contains("\"@type\":\"MIREOT\""));
    assertTrue(json.contains("\"upperTerms\""));
    assertTrue(json.contains("\"lowerTerms\""));
    assertTrue(json.contains("\"branchFromTerms\""));
    assertTrue(json.contains("GO:0008150"));
    assertTrue(json.contains("GO:0009987"));
    assertTrue(json.contains("GO:0008152"));
  }

  /**
   * Tests MireotExtractStrategy deserialization from JSON with type discriminator.
   */
  @Test
  void testMireotExtractStrategyDeserialization() throws Exception {
    String json = "{\"@type\":\"MIREOT\",\"upperTerms\":[\"GO:0008150\"],\"lowerTerms\":[\"GO:0009987\"],\"branchFromTerms\":[]}";
    ExtractStrategy strategy = objectMapper.readValue(json, ExtractStrategy.class);

    assertNotNull(strategy);
    assertInstanceOf(MireotExtractStrategy.class, strategy);
    MireotExtractStrategy mireot = (MireotExtractStrategy) strategy;
    assertEquals(1, mireot.upperTerms().size());
    assertEquals("GO:0008150", mireot.upperTerms().get(0));
    assertEquals(1, mireot.lowerTerms().size());
    assertEquals("GO:0009987", mireot.lowerTerms().get(0));
    assertEquals(0, mireot.branchFromTerms().size());
  }

  /**
   * Tests SubsetExtractStrategy serialization with type discriminator.
   */
  @Test
  void testSubsetExtractStrategySerialization() throws Exception {
    SubsetExtractStrategy strategy = new SubsetExtractStrategy(List.of("go_slim"));
    String json = objectMapper.writeValueAsString(strategy);

    assertNotNull(json);
    assertTrue(json.contains("\"@type\":\"Subset\""));
    assertTrue(json.contains("\"terms\""));
    assertTrue(json.contains("go_slim"));
  }

  /**
   * Tests SubsetExtractStrategy deserialization from JSON with type discriminator.
   */
  @Test
  void testSubsetExtractStrategyDeserialization() throws Exception {
    String json = "{\"@type\":\"Subset\",\"terms\":[\"go_slim\"]}";
    ExtractStrategy strategy = objectMapper.readValue(json, ExtractStrategy.class);

    assertNotNull(strategy);
    assertInstanceOf(SubsetExtractStrategy.class, strategy);
    SubsetExtractStrategy subset = (SubsetExtractStrategy) strategy;
    assertEquals(1, subset.terms().size());
    assertEquals("go_slim", subset.terms().get(0));
  }

  /**
   * Tests round-trip serialization for all extract strategy types.
   */
  @Test
  void testExtractStrategyRoundTrip() throws Exception {
    ExtractStrategy[] strategies = {
        new SlmeExtractStrategy(SlmeExtractMethod.BOT, List.of("GO:0008150")),
        new MireotExtractStrategy(List.of("GO:0008150"), List.of("GO:0009987"), List.of()),
        new SubsetExtractStrategy(List.of("go_slim"))};

    for (ExtractStrategy original : strategies) {
      String json = objectMapper.writeValueAsString(original);
      ExtractStrategy deserialized = objectMapper.readValue(json, ExtractStrategy.class);

      assertNotNull(deserialized);
      assertEquals(original.getClass(), deserialized.getClass());
      // Verify args are equivalent
      assertEquals(original.getArgs(), deserialized.getArgs());
    }
  }
}
