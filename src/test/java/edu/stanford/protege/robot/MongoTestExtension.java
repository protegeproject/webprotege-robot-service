package edu.stanford.protege.robot;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public class MongoTestExtension implements BeforeAllCallback, AfterAllCallback {

  private static final Logger logger = LoggerFactory.getLogger(MongoTestExtension.class);

  private static MongoDBContainer mongoDBContainer;

  @Override
  public void beforeAll(ExtensionContext extensionContext) throws Exception {
    if (mongoDBContainer == null) {
      var imageName = DockerImageName.parse("mongo:7.0");
      mongoDBContainer = new MongoDBContainer(imageName);
      mongoDBContainer.start();

      var replicaSetUrl = mongoDBContainer.getReplicaSetUrl();
      logger.info("MongoDB started with URI: {}", replicaSetUrl);
      System.setProperty("spring.data.mongodb.uri", replicaSetUrl);
    }
  }

  @Override
  public void afterAll(ExtensionContext extensionContext) throws Exception {
    // Don't stop the container here as it may be shared across test classes
    // It will be stopped automatically when the JVM exits
  }

  /**
   * Get the MongoDB container instance for use in tests.
   *
   * @return the MongoDB container
   */
  public static MongoDBContainer getMongoDBContainer() {
    return mongoDBContainer;
  }
}
