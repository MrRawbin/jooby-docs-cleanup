/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import io.jooby.MediaType;
import io.jooby.SneakyThrows;
import io.jooby.handler.Asset;
import io.jooby.handler.AssetSource;

public class ClassPathAssetSourceTest {

  @Test
  public void disallowedAccessToRootClasspath() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new ClassPathAssetSource(getClass().getClassLoader(), null));
    assertThrows(
        IllegalArgumentException.class,
        () -> new ClassPathAssetSource(getClass().getClassLoader(), ""));
    assertThrows(
        IllegalArgumentException.class,
        () -> new ClassPathAssetSource(getClass().getClassLoader(), "  "));
    assertThrows(
        IllegalArgumentException.class,
        () -> new ClassPathAssetSource(getClass().getClassLoader(), "/"));
    assertThrows(
        IllegalArgumentException.class,
        () -> new ClassPathAssetSource(getClass().getClassLoader(), " / "));
  }

  @Test
  public void checkclasspathFiles() {
    assetSource(
        "/META-INF/resources/webjars/vue/" + VUE,
        source -> {
          Asset vuejs = source.resolve("dist/vue.cjs.js");
          assertNotNull(vuejs);
          assertEquals(MediaType.js, vuejs.getContentType());

          Asset packagejson = source.resolve("package.json");
          assertNotNull(packagejson);
          assertEquals(MediaType.json, packagejson.getContentType());

          Asset root = source.resolve("");
          assertNull(root);
        });

    assetSource(
        "/META-INF/resources/webjars/vue/" + VUE + "/dist",
        source -> {
          Asset vuejs = source.resolve("vue.cjs.js");
          assertNotNull(vuejs);
          assertEquals(MediaType.js, vuejs.getContentType());

          Asset root = source.resolve("");
          assertNull(root);
        });

    assetSource(
        "/META-INF/resources/webjars/vue/" + VUE + "/dist/vue.cjs.js",
        source -> {
          Asset vuejs = source.resolve("vue.cjs.js");
          assertNotNull(vuejs);
          assertEquals(MediaType.js, vuejs.getContentType());
        });

    assetSource(
        "/log",
        source -> {
          Asset logback = source.resolve("logback.xml");
          assertNotNull(logback);
          assertEquals(MediaType.xml, logback.getContentType());
        });
  }

  private void assetSource(String location, Consumer<AssetSource> consumer) {
    AssetSource source = new ClassPathAssetSource(getClass().getClassLoader(), location);
    consumer.accept(source);
  }

  private static final String VUE = vueVersion();

  private static String vueVersion() {
    try (InputStream vueprops =
        ClassPathAssetSourceTest.class
            .getClassLoader()
            .getResourceAsStream("META-INF/maven/org.webjars.npm/vue/pom.properties")) {
      Properties properties = new Properties();
      properties.load(vueprops);
      return properties.getProperty("version");
    } catch (IOException x) {
      throw SneakyThrows.propagate(x);
    }
  }
}
