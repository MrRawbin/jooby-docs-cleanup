/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package examples;

import java.util.List;

import io.jooby.annotation.GET;
import io.jooby.annotation.Path;
import io.jooby.annotation.PathParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Path("/openapi")
public class OpenApiController {

  @GET
  @Operation(operationId = "foo", tags = "a", description = "description", summary = "summary")
  @ApiResponses({
    @ApiResponse(
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = Person.class)))),
    @ApiResponse(responseCode = "400")
  })
  public List<Person> list() {
    return null;
  }

  @GET("/{id}")
  @Operation(
      summary = "Find Person by ID",
      responses =
          @ApiResponse(
              description = "Found person",
              headers = @Header(name = "Token", schema = @Schema(implementation = String.class))))
  public Person find(@PathParam long id) {
    return null;
  }
}
