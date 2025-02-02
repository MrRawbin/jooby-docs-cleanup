/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.adoc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockProcessor;
import org.asciidoctor.extension.Reader;

public class DependencyProcessor extends BlockProcessor {

  public DependencyProcessor(String name, Map<String, Object> config) throws IOException {
    super(name, config);
  }

  @Override
  public Object process(StructuralNode parent, Reader reader, Map<String, Object> attributes) {
    try {
      List<String> lines = new ArrayList<>();
      String[] artifactId = ((String) attributes.get("artifactId")).split("\\s*,\\s*");

      maven(
          (String) attributes.get("groupId"),
          artifactId,
          (String) attributes.get("version"),
          lines::add);

      lines.add("");

      gradle(
          (String) attributes.get("groupId"),
          artifactId,
          (String) attributes.get("version"),
          lines::add);
      lines.add("");

      parseContent(parent, lines);
      return null;
    } catch (Exception error) {
      throw new IllegalStateException(">>" + parent.getId(), error);
    }
  }

  private String groupId(String artifactId) {
    return Dependencies.get(artifactId).groupId;
  }

  private String version(String artifactId) {
    return Dependencies.get(artifactId).version;
  }

  private void gradle(String groupId, String[] artifactId, String version, Consumer<String> lines) {
    lines.accept(".Gradle");
    lines.accept("[source,javascript,role=\"secondary\"]");
    lines.accept("----");
    for (int i = 0; i < artifactId.length; i++) {
      if (i > 0) {
        lines.accept("");
      }
      comment(artifactId[i], "//", "").ifPresent(lines::accept);
      lines.accept(
          "implementation '"
              + (groupId == null ? groupId(artifactId(artifactId[i])) : groupId)
              + ":"
              + artifactId(artifactId[i])
              + ":"
              + (version == null ? version(artifactId(artifactId[i])) : version)
              + "'");
    }
    lines.accept("----");
  }

  private String artifactId(String artifactId) {
    int s = artifactId.indexOf(":");
    return s > 0 ? artifactId.substring(0, s) : artifactId;
  }

  private Optional<String> comment(String text, String prefix, String suffix) {
    int s = text.indexOf(":");
    return s > 0 ? Optional.of(prefix + " " + text.substring(s + 1) + suffix) : Optional.empty();
  }

  private void maven(String groupId, String[] artifactId, String version, Consumer<String> lines) {
    lines.accept(".Maven");
    lines.accept("[source, xml,role=\"primary\"]");
    lines.accept("----");
    for (int i = 0; i < artifactId.length; i++) {
      if (i > 0) {
        lines.accept("");
      }
      comment(artifactId[i], "<!--", "-->").ifPresent(lines::accept);
      lines.accept("<dependency>");
      lines.accept(
          "  <groupId>"
              + (groupId == null ? groupId(artifactId(artifactId[i])) : groupId)
              + "</groupId>");
      lines.accept("  <artifactId>" + artifactId(artifactId[i]) + "</artifactId>");
      lines.accept(
          "  <version>"
              + (version == null ? version(artifactId(artifactId[i])) : version)
              + "</version>");
      lines.accept("</dependency>");
    }
    lines.accept("----");
  }
}
