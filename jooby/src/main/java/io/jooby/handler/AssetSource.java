/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.handler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.jooby.SneakyThrows;
import io.jooby.internal.ClassPathAssetSource;
import io.jooby.internal.FileDiskAssetSource;
import io.jooby.internal.FolderDiskAssetSource;

/**
 * An asset source is a collection or provider of {@link Asset}. There are two implementations:
 *
 * <ul>
 *   <li>File system: using {@link #create(Path)}.
 *   <li>Classpath/URL: using {@link #create(ClassLoader, String)}.
 * </ul>
 */
public interface AssetSource {

  /**
   * Resolve an asset using the given path.
   *
   * @param path Path to look for.
   * @return An asset or <code>null</code>.
   */
  @Nullable Asset resolve(@NonNull String path);

  /**
   * Classpath asset source. Useful for resolving files from classpath (including jar files).
   *
   * @param loader Class loader.
   * @param location Classpath location. For security reasons root of classpath <code>/</code> is
   *     disallowed.
   * @return An asset source.
   */
  static @NonNull AssetSource create(@NonNull ClassLoader loader, @NonNull String location) {
    return new ClassPathAssetSource(loader, location);
  }

  /**
   * Creates a webjar asset source. Usage:
   *
   * <ul>
   *   <li>Add a webjar to your project, for example swagger-ui
   *   <li>Create and add an asset handler:
   *       <pre>{@code
   * asset("/path/*", AssetSource.webjar(getClassLoader(), "swagger-ui"));
   *
   *
   * }</pre>
   * </ul>
   *
   * @param loader Class loader.
   * @param name Web asset name.
   * @return A webjar source.
   */
  static @NonNull AssetSource webjars(@NonNull ClassLoader loader, @NonNull String name) {
    List<String> location =
        Arrays.asList(
            "META-INF/maven/org.webjars/" + name + "/pom.properties",
            "META-INF/maven/org.webjars.npm/" + name + "/pom.properties");
    String versionPath =
        location.stream()
            .filter(it -> loader.getResource(it) != null)
            .findFirst()
            .orElseThrow(
                () -> SneakyThrows.propagate(new FileNotFoundException(location.toString())));
    try (InputStream in = loader.getResourceAsStream(versionPath)) {
      Properties properties = new Properties();
      properties.load(in);
      String version = properties.getProperty("version");
      String source = "/META-INF/resources/webjars/" + name + "/" + version;
      return new ClassPathAssetSource(loader, source);
    } catch (IOException x) {
      throw SneakyThrows.propagate(x);
    }
  }

  /**
   * Creates a source from given location. Assets are resolved from file system.
   *
   * @param location Asset directory.
   * @return A new file system asset source.
   */
  static @NonNull AssetSource create(@NonNull Path location) {
    Path absoluteLocation = location.toAbsolutePath();
    if (Files.isDirectory(absoluteLocation)) {
      return new FolderDiskAssetSource(absoluteLocation);
    } else if (Files.isRegularFile(location)) {
      return new FileDiskAssetSource(location);
    }
    throw SneakyThrows.propagate(new FileNotFoundException(location.toAbsolutePath().toString()));
  }
}
