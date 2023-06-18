/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.i2367;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.jooby.exception.BadRequestException;
import io.jooby.handler.AccessLogHandler;
import io.jooby.junit.ServerTest;
import io.jooby.junit.ServerTestRunner;

public class Issue2367 {

  @ServerTest
  public void shouldAfterHandlerGetTheRightCode(ServerTestRunner runner) {
    runner
        .define(
            app -> {
              app.after(
                  (ctx, result, failure) -> {
                    ctx.send(
                        "statusCode: "
                            + ctx.getResponseCode().value()
                            + ", result: "
                            + result
                            + ", failure: "
                            + failure.getClass().getSimpleName());
                  });

              app.get(
                  "/2367",
                  ctx -> {
                    throw new BadRequestException("intentional error");
                  });
            })
        .ready(
            http -> {
              http.get(
                  "/2367",
                  rsp -> {
                    assertEquals(
                        "statusCode: 400, result: null, failure: BadRequestException",
                        rsp.body().string());
                  });
            });
  }

  @ServerTest
  public void shouldLog404(ServerTestRunner runner) {
    runner
        .define(
            app -> {
              app.use(new AccessLogHandler().log(app.getLog()::debug));

              app.assets("/2367/*", "/2367");
            })
        .ready(
            http -> {
              http.get(
                  "/2367/index.js",
                  rsp -> {
                    assertEquals(404, rsp.code());
                  });
            });
  }
}
