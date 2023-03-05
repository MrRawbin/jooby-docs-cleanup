/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.junit;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.jooby.ExecutionMode;
import io.jooby.Jooby;
import io.jooby.Server;
import io.jooby.ServerOptions;
import io.jooby.SneakyThrows;
import io.jooby.test.WebClient;

public class ServerTestRunner {

  static {
    System.setProperty("jooby.useShutdownHook", "false");
  }

  private final String testName;

  private final ServerProvider server;

  private final ExecutionMode executionMode;

  private Supplier<Jooby> provider;

  private boolean followRedirects = true;

  public ServerTestRunner(String testName, ServerProvider server, ExecutionMode executionMode) {
    this.testName = testName;
    this.server = server;
    this.executionMode = executionMode;
  }

  public ServerTestRunner define(Consumer<Jooby> consumer) {
    use(
        () -> {
          Jooby app = new Jooby();
          consumer.accept(app);
          return app;
        });
    return this;
  }

  public ServerTestRunner use(Supplier<Jooby> provider) {
    this.provider = provider;
    return this;
  }

  public void ready(SneakyThrows.Consumer<WebClient> onReady) {
    ready((http, https) -> onReady.accept(http));
  }

  public void ready(SneakyThrows.Consumer2<WebClient, WebClient> onReady) {
    Server server = this.server.get();
    try {
      System.setProperty("___app_name__", testName);
      System.setProperty("___server_name__", server.getName());
      Jooby app = provider.get();
      Optional.ofNullable(executionMode).ifPresent(app::setExecutionMode);
      ServerOptions serverOptions = app.getServerOptions();
      if (serverOptions != null) {
        server.setOptions(serverOptions);
      }
      // HTTP/2 is off while testing unless explicit set
      ServerOptions options = server.getOptions();
      options.setHttp2(Optional.ofNullable(options.isHttp2()).orElse(Boolean.FALSE));
      options.setPort(Integer.parseInt(System.getenv().getOrDefault("BUILD_PORT", "9999")));
      WebClient https;
      if (options.isSSLEnabled()) {
        options.setSecurePort(
            Integer.parseInt(System.getenv().getOrDefault("BUILD_SECURE_PORT", "9443")));
        https = new WebClient("https", options.getSecurePort(), followRedirects);
      } else {
        https = null;
      }
      server.start(app);
      try (WebClient http = new WebClient("http", options.getPort(), followRedirects)) {
        onReady.accept(http, https);
      } finally {
        if (https != null) {
          https.close();
        }
      }
    } catch (AssertionError x) {
      throw SneakyThrows.propagate(serverInfo(x));
    } finally {
      server.stop();
    }
  }

  public String getServer() {
    return server.getName();
  }

  public ServerTestRunner dontFollowRedirects() {
    followRedirects = false;
    return this;
  }

  public boolean matchesEventLoopThread(String threadName) {
    return server.matchesEventLoopThread(threadName);
  }

  public Path resolvePath(String... segments) {
    Path path = Paths.get(System.getProperty("user.dir"));
    for (String segment : segments) {
      path = path.resolve(segment);
    }
    return path;
  }

  @Override
  public String toString() {
    StringBuilder message = new StringBuilder();
    message.append(testName).append("(");
    message.append(getServer().toLowerCase());
    if (executionMode != null) {
      message.append(", ").append(executionMode);
    }
    message.append(")");
    return message.toString();
  }

  private AssertionError serverInfo(AssertionError x) {
    StringBuilder message = new StringBuilder();
    message.append(this);
    Optional.ofNullable(x.getMessage()).ifPresent(m -> message.append(": ").append(m));
    AssertionError error = new AssertionError(message.toString().trim());
    error.setStackTrace(x.getStackTrace());
    return error;
  }
}
