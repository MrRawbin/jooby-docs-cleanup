/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package source;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import io.jooby.Context;
import io.jooby.MediaType;
import io.jooby.annotation.Consumes;
import io.jooby.annotation.GET;
import io.jooby.annotation.Produces;

@Produces("text/produces")
@Consumes("text/consumes")
public class RouteWithMimeTypes {

  @GET(value = "/consumes", consumes = "application/json")
  public String doIt(Context ctx) {
    assertEquals(Arrays.asList(MediaType.json), ctx.getRoute().getConsumes());
    return ctx.getRequestPath();
  }

  @GET(
      value = "/consumes2",
      consumes = {"application/json", "application/xml"})
  public String consumes2(Context ctx) {
    assertEquals(Arrays.asList(MediaType.json, MediaType.xml), ctx.getRoute().getConsumes());
    return ctx.getRequestPath();
  }

  @GET(value = "/produces", produces = "text/html")
  public String produces(Context ctx) {
    assertEquals(Arrays.asList(MediaType.html), ctx.getRoute().getProduces());
    return ctx.getRequestPath();
  }

  @GET(value = "/consumes/produces", consumes = "text/html", produces = "text/html")
  public String consumesProduces(Context ctx) {
    assertEquals(Arrays.asList(MediaType.html), ctx.getRoute().getConsumes());
    assertEquals(Arrays.asList(MediaType.html), ctx.getRoute().getProduces());
    return ctx.getRequestPath();
  }

  @GET("/method/produces")
  @Produces("text/plain")
  public String methodProduces(Context ctx) {
    assertEquals(Arrays.asList(MediaType.text), ctx.getRoute().getProduces());
    return ctx.getRequestPath();
  }

  @GET("/method/consumes")
  @Consumes("m/consumes")
  public String methodConsumes(Context ctx) {
    assertEquals(Arrays.asList(MediaType.valueOf("m/consumes")), ctx.getRoute().getConsumes());
    return ctx.getRequestPath();
  }

  @GET("/class/produces")
  public String classProduces(Context ctx) {
    assertEquals(Arrays.asList(MediaType.valueOf("text/produces")), ctx.getRoute().getProduces());
    return ctx.getRequestPath();
  }

  @GET("/class/consumes")
  public String classConsumes(Context ctx) {
    assertEquals(Arrays.asList(MediaType.valueOf("text/consumes")), ctx.getRoute().getConsumes());
    return ctx.getRequestPath();
  }
}
