package edu.stanford.protege.robot.pipeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Represents a relative file path within a repository structure.
 *
 * <p>
 * This value object ensures that file paths are:
 *
 * <ul>
 * <li>Relative (not absolute)
 * <li>Safe (no path traversal with "..")
 * <li>Non-empty
 * </ul>
 *
 * <p>
 * Examples of valid paths:
 *
 * <ul>
 * <li>"ontology.owl" - file in root
 * <li>"src/ontology.owl" - file in src directory
 * <li>"data/models/pizza.ttl" - nested file path
 * </ul>
 */
public record RelativePath(@Nonnull String value) {

  public RelativePath {
    Objects.requireNonNull(value, "File path cannot be null");
    if (value.trim().isEmpty()) {
      throw new IllegalArgumentException("File path cannot be empty");
    }
    if (value.startsWith("/") || value.startsWith("\\")) {
      throw new IllegalArgumentException("File path must be relative, not absolute");
    }
    if (value.contains("..")) {
      throw new IllegalArgumentException("File path cannot contain path traversal (..)");
    }
    if (value.contains("\\")) {
      throw new IllegalArgumentException("File path must use forward slashes (/)");
    }
  }

  @JsonCreator
  @Nonnull
  public static RelativePath create(@Nonnull String value) {
    return new RelativePath(value);
  }

  /**
   * Returns the file name (last component of the path).
   *
   * @return the file name
   */
  public String getFileName() {
    return Paths.get(value).getFileName().toString();
  }

  /**
   * Returns the directory path (all components except the last).
   *
   * @return the directory path, or empty string if file is in root
   */
  public String getDirectory() {
    Path path = Paths.get(value);
    Path parent = path.getParent();
    return parent != null ? parent.toString() : "";
  }

  /**
   * Returns the normalized path as a Path object.
   *
   * @return the path as a Path object
   */
  public Path asPath() {
    return Paths.get(value);
  }

  @JsonValue
  @Nonnull
  public String asString() {
    return value;
  }
}
