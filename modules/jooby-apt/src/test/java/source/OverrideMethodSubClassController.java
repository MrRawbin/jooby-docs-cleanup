/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package source;

import io.jooby.annotation.GET;
import io.jooby.annotation.Path;
import io.jooby.annotation.QueryParam;

@Path("/overrideMethod")
public class OverrideMethodSubClassController extends BaseController {

  @Override
  public String base() {
    return super.base();
  }

  @GET("/newpath")
  @Override
  public String withPath(@QueryParam String q) {
    return super.withPath(q);
  }
}
