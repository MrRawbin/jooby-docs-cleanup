/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.jooby.MediaType;
import io.jooby.SneakyThrows;
import io.jooby.handler.Asset;

public class JarAsset implements Asset {

  private final ZipEntry entry;
  private final JarFile jar;

  public JarAsset(JarURLConnection connection) throws IOException {
    connection.setUseCaches(false);
    jar = connection.getJarFile();
    entry = jar.getEntry(connection.getEntryName());
  }

  @Override
  public boolean isDirectory() {
    return entry.isDirectory();
  }

  @Override
  public long getSize() {
    return entry.getSize();
  }

  @Override
  public long getLastModified() {
    return entry.getTime();
  }

  @NonNull @Override
  public MediaType getContentType() {
    return MediaType.byFile(entry.getName());
  }

  @Override
  public InputStream stream() {
    try {
      return jar.getInputStream(entry);
    } catch (IOException x) {
      throw SneakyThrows.propagate(x);
    }
  }

  @Override
  public void close() {
    try {
      jar.close();
    } catch (Exception x) {
      // silence
    }
  }
}
