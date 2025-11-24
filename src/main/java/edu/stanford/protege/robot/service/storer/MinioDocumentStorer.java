package edu.stanford.protege.robot.service.storer;

import edu.stanford.protege.robot.service.exception.StorageException;
import edu.stanford.protege.webprotege.common.BlobLocation;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.springframework.stereotype.Component;

@Component
public class MinioDocumentStorer {

  private final MinioClient minioClient;

  private final MinioProperties minioProperties;

  public MinioDocumentStorer(
      @Nonnull MinioClient minioClient, @Nonnull MinioProperties minioProperties) {
    this.minioClient = Objects.requireNonNull(minioClient, "minioClient cannot be null");
    this.minioProperties = Objects.requireNonNull(minioProperties, "minioProperties cannot be null");
  }

  /**
   * Stores a document file in MinIO object storage and returns its blob location.
   *
   * <p>
   * This method uploads a local file to MinIO storage, automatically creating the bucket if it
   * doesn't exist. The document is stored as a binary object with the content type
   * "application/octet-stream". A unique blob location is generated for each upload to ensure
   * proper organization and retrieval of stored documents.
   *
   * @param documentPath
   *          the path to the local file to be uploaded to MinIO storage
   * @return a {@link BlobLocation} containing the bucket name and object key where the document has
   *         been stored
   * @throws StorageException
   *           if any error occurs during the upload process, including network
   *           issues, authentication failures, or MinIO server errors
   */
  public BlobLocation storeDocument(String documentPath) {
    try {
      var location = generateBlobLocation();
      // Create bucket if necessary
      createBucketIfNecessary(location);
      minioClient.uploadObject(UploadObjectArgs.builder()
          .filename(documentPath)
          .bucket(location.bucket())
          .object(location.name())
          .contentType(determineContentType(documentPath))
          .build());
      return location;
    } catch (ErrorResponseException
        | XmlParserException
        | ServerException
        | NoSuchAlgorithmException
        | IOException
        | InvalidResponseException
        | InvalidKeyException
        | InternalException
        | InsufficientDataException e) {
      throw new StorageException("Problem writing revision history document to storage " + documentPath, e);
    }
  }

  private void createBucketIfNecessary(BlobLocation location)
      throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException,
      InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException,
      XmlParserException {
    if (!minioClient.bucketExists(
        BucketExistsArgs.builder().bucket(location.bucket()).build())) {
      minioClient.makeBucket(
          MakeBucketArgs.builder().bucket(location.bucket()).build());
    }
  }

  private BlobLocation generateBlobLocation() {
    return new BlobLocation(minioProperties.getRobotOutputDocumentsBucketName(), generateObjectName());
  }

  private static String generateObjectName() {
    return "robot-output-" + UUID.randomUUID();
  }

  private static String determineContentType(String filePath) {
    if (filePath == null || filePath.isEmpty()) {
      return "application/octet-stream";
    }

    var lowercasePath = filePath.toLowerCase(Locale.ROOT);
    var extension = getFileExtension(lowercasePath);

    // ROBOT convert formats
    return switch (extension) {
      case "json" -> "application/json";
      case "obo" -> "text/obo";             // W3C-Specified (not IANA-registered)
      case "ofn" -> "text/owl-functional";  // W3C-Specified (not IANA-registered)
      case "omn" -> "text/owl-manchester";  // W3C-Specified (not IANA-registered)
      case "owl" -> "application/rdf+xml";
      case "owx" -> "application/owl+xml";  // W3C-Specified (not IANA-registered)
      case "ttl" -> "text/turtle";
      default -> "application/octet-stream";
    };
  }

  private static String getFileExtension(String filePath) {
    var lastDotIndex = filePath.lastIndexOf('.');
    if (lastDotIndex == -1 || lastDotIndex == filePath.length() - 1) {
      return "";
    }
    return filePath.substring(lastDotIndex + 1);
  }
}
