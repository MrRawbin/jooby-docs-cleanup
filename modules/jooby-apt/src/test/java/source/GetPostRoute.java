/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package source;

import io.jooby.Context;
import io.jooby.annotation.GET;
import io.jooby.annotation.POST;
import io.jooby.annotation.Path;

@Path("/")
public class GetPostRoute {

  @GET
  @POST
  public String getIt() {
    return "Got it!";
  }

  @GET
  @Path("/subpath")
  public String subpath() {
    return "OK";
  }

  @GET
  @Path("/void")
  public void noContent() {}

  @GET
  @Path("/voidwriter")
  public void writer(Context ctx) throws Exception {
    ctx.responseWriter(
        writer -> {
          writer.println("writer");
        });
  }
}
