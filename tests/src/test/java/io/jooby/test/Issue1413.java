/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Duration;

import io.jooby.handler.Cors;
import io.jooby.handler.CorsHandler;
import io.jooby.junit.ServerTest;
import io.jooby.junit.ServerTestRunner;

public class Issue1413 {

  @ServerTest
  public void shouldDoPreflightWithCredentials(ServerTestRunner runner) {
    runner
        .define(
            app -> {
              app.use(
                  new CorsHandler(
                      new Cors()
                          .setMethods("*")
                          .setOrigin("http://foo.com")
                          .setUseCredentials(true)));

              app.put("/api/v1/machines/{key}", ctx -> ctx.path("key").value());
              app.post("/api/v1/machines/{key}", ctx -> ctx.path("key").value());
              app.get("/api/v1/machines/{key}", ctx -> ctx.path("key").value());
            })
        .ready(
            client -> {
              // OPTIONS (Pre-flight), checking PUT Method => OK and Access Control Headers Present
              client
                  .header("Origin", "http://foo.com")
                  .header("Access-Control-Request-Method", "PUT")
                  .options(
                      "/api/v1/machines/123",
                      rsp -> {
                        assertEquals("", rsp.body().string());
                        assertEquals(200, rsp.code());
                        assertEquals("http://foo.com", rsp.header("Access-Control-Allow-Origin"));
                        assertEquals("true", rsp.header("Access-Control-Allow-Credentials"));
                      });

              // POST Method by allowed origin => OK and Access Control Headers Present
              client
                  .header("Origin", "http://foo.com")
                  .post(
                      "/api/v1/machines/123",
                      rsp -> {
                        assertEquals("123", rsp.body().string());
                        assertEquals(200, rsp.code());
                        assertEquals("http://foo.com", rsp.header("Access-Control-Allow-Origin"));
                        assertEquals("true", rsp.header("Access-Control-Allow-Credentials"));
                      });

              // Origin different from the allowed one => Forbidden
              client
                  .header("Origin", "http://bar.com")
                  .get(
                      "/api/v1/machines/123",
                      rsp -> {
                        assertEquals(403, rsp.code());
                        assertEquals("", rsp.body().string());
                        assertNull(rsp.header("Access-Control-Allow-Origin"));
                        assertNull(rsp.header("Access-Control-Allow-Credentials"));
                      });

              // PUT Method and allowed origin => OK and Access Control Headers Present
              client
                  .header("Origin", "http://foo.com")
                  .put(
                      "/api/v1/machines/123",
                      rsp -> {
                        assertEquals("123", rsp.body().string());
                        assertEquals(200, rsp.code());
                        assertEquals("http://foo.com", rsp.header("Access-Control-Allow-Origin"));
                        assertEquals("true", rsp.header("Access-Control-Allow-Credentials"));
                      });
            });
  }

  @ServerTest
  public void shouldDoPreflightWithoutCredentials(ServerTestRunner runner) {
    runner
        .define(
            app -> {
              app.use(
                  new CorsHandler(
                      new Cors()
                          .setMethods("*")
                          .setOrigin("http://foo.com")
                          .setUseCredentials(false)));

              app.put("/api/v1/machines/{key}", ctx -> ctx.path("key").value());
              app.post("/api/v1/machines/{key}", ctx -> ctx.path("key").value());
              app.get("/api/v1/machines/{key}", ctx -> ctx.path("key").value());
            })
        .ready(
            client -> {
              // OPTIONS (Pre-flight), checking PUT Method => OK and Access Control Headers Present
              client
                  .header("Origin", "http://foo.com")
                  .header("Access-Control-Request-Method", "PUT")
                  .options(
                      "/api/v1/machines/123",
                      rsp -> {
                        assertEquals("", rsp.body().string());
                        assertEquals(200, rsp.code());
                        assertEquals("http://foo.com", rsp.header("Access-Control-Allow-Origin"));
                        assertNull(rsp.header("Access-Control-Allow-Credentials"));
                      });

              // POST Method by allowed origin => OK and Access Control Headers Present
              client
                  .header("Origin", "http://foo.com")
                  .post(
                      "/api/v1/machines/123",
                      rsp -> {
                        assertEquals("123", rsp.body().string());
                        assertEquals(200, rsp.code());
                        assertEquals("http://foo.com", rsp.header("Access-Control-Allow-Origin"));
                        assertNull(rsp.header("Access-Control-Allow-Credentials"));
                      });

              // Origin different from the allowed one => Forbidden
              client
                  .header("Origin", "http://bar.com")
                  .get(
                      "/api/v1/machines/123",
                      rsp -> {
                        assertEquals(403, rsp.code());
                        assertEquals("", rsp.body().string());
                        assertNull(rsp.header("Access-Control-Allow-Origin"));
                        assertNull(rsp.header("Access-Control-Allow-Credentials"));
                      });

              // PUT Method and allowed origin => OK and Access Control Headers Present
              client
                  .header("Origin", "http://foo.com")
                  .put(
                      "/api/v1/machines/123",
                      rsp -> {
                        assertEquals("123", rsp.body().string());
                        assertEquals(200, rsp.code());
                        assertEquals("http://foo.com", rsp.header("Access-Control-Allow-Origin"));
                        assertNull(rsp.header("Access-Control-Allow-Credentials"));
                      });
            });
  }

  @ServerTest
  public void shouldDoPreflightWithCustomMethods(ServerTestRunner runner) {
    runner
        .define(
            app -> {
              app.use(
                  new CorsHandler(
                      new Cors()
                          .setMethods("GET", "POST", "HEAD", "OPTIONS")
                          .setOrigin("http://foo.com")
                          .setExposedHeaders("*")
                          .setMaxAge(Duration.ofMinutes(30))
                          .setHeaders("*")));

              app.put("/api/v1/machines/{key}", ctx -> ctx.path("key").value());
              app.post("/api/v1/machines/{key}", ctx -> ctx.path("key").value());
              app.get("/api/v1/machines/{key}", ctx -> ctx.path("key").value());
            })
        .ready(
            client -> {
              // OPTIONS (Pre-flight), checking PUT Method => OK and Access Control Headers Present
              client
                  .header("Origin", "http://foo.com")
                  .header("Access-Control-Request-Method", "PUT")
                  .options(
                      "/api/v1/machines/123",
                      rsp -> {
                        assertEquals("", rsp.body().string());
                        assertEquals(403, rsp.code());
                        assertNull(rsp.header("Access-Control-Allow-Origin"));
                        assertNull(rsp.header("Access-Control-Allow-Credentials"));
                        assertNull(rsp.header("Access-Control-Allow-Methods"));
                      });

              // OPTIONS (Pre-flight), checking PUT Method => OK and Access Control Headers Present
              client
                  .header("Origin", "http://foo.com")
                  .header("Access-Control-Request-Method", "GET")
                  .options(
                      "/api/v1/machines/123",
                      rsp -> {
                        assertEquals("", rsp.body().string());
                        assertEquals(200, rsp.code());
                        assertEquals("http://foo.com", rsp.header("Access-Control-Allow-Origin"));
                        assertEquals("true", rsp.header("Access-Control-Allow-Credentials"));
                        assertEquals(
                            "GET,POST,HEAD,OPTIONS", rsp.header("Access-Control-Allow-Methods"));
                      });
            });
  }
}
