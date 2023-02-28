/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.i1805;

import java.net.URI;
import java.net.URL;

import io.jooby.annotation.GET;
import io.jooby.annotation.Path;
import io.jooby.annotation.QueryParam;

@Path("/1805")
public class C1805 {
  @GET("/uri")
  public URI uri(@QueryParam URI param) {
    return param;
  }

  @GET("/url")
  public URL url(@QueryParam URL param) {
    return param;
  }
}
