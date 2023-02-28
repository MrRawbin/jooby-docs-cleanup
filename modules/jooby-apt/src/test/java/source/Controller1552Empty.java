/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package source;

import io.jooby.Context;
import io.jooby.annotation.GET;
import io.jooby.annotation.Path;

@Path("/inherit_empty")
public class Controller1552Empty extends Controller1552Base {
  @GET
  public String fake(Context ctx) {
    return ctx.getRequestPath();
  }
}
