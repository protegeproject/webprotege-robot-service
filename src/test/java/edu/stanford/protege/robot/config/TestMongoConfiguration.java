package edu.stanford.protege.robot.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * Test configuration for MongoDB to handle special characters in map keys.
 * Configures the MappingMongoConverter to replace dots in map keys with a Unicode character.
 */
@TestConfiguration
public class TestMongoConfiguration {

  /**
   * Configure MongoDB custom conversions.
   */
  @Bean
  public MongoCustomConversions mongoCustomConversions() {
    return new MongoCustomConversions(java.util.Collections.emptyList());
  }

  /**
   * Configure MappingMongoConverter with dot replacement for map keys.
   * MongoDB doesn't allow dots in document field names, so we replace them with a Unicode character.
   *
   * @param mongoDbFactory
   *          MongoDB database factory
   * @param mongoMappingContext
   *          MongoDB mapping context
   * @param mongoCustomConversions
   *          MongoDB custom conversions
   * @return configured MappingMongoConverter
   */
  @Bean
  public MappingMongoConverter mappingMongoConverter(
      MongoDatabaseFactory mongoDbFactory,
      MongoMappingContext mongoMappingContext,
      MongoCustomConversions mongoCustomConversions) {

    DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
    MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);

    // Configure custom conversions
    converter.setCustomConversions(mongoCustomConversions);

    // Remove the _class field from documents
    converter.setTypeMapper(new DefaultMongoTypeMapper(null));

    // Enable map key dot replacement - replace dots with a safe Unicode character
    converter.setMapKeyDotReplacement("_DOT_");

    converter.afterPropertiesSet();

    return converter;
  }
}
