/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package source;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.jooby.Context;
import io.jooby.FileUpload;
import io.jooby.FlashMap;
import io.jooby.Formdata;
import io.jooby.QueryString;
import io.jooby.Session;
import io.jooby.StatusCode;
import io.jooby.annotation.CookieParam;
import io.jooby.annotation.DELETE;
import io.jooby.annotation.FlashParam;
import io.jooby.annotation.FormParam;
import io.jooby.annotation.GET;
import io.jooby.annotation.HeaderParam;
import io.jooby.annotation.POST;
import io.jooby.annotation.Path;
import io.jooby.annotation.PathParam;
import io.jooby.annotation.QueryParam;

@Path("/p")
public class Provisioning {

  @GET("/noarg")
  public String noarg() {
    return "noarg";
  }

  @GET("/context")
  public String context(Context ctx) {
    assertTrue(ctx instanceof Context);
    return "ctx";
  }

  @GET("/contextFirst")
  public String contextFirst(Context ctx, QueryString queryString) {
    assertTrue(ctx instanceof Context);
    assertTrue(queryString instanceof QueryString);
    return "ctxfirst";
  }

  @GET("/queryString")
  public String queryString(QueryString queryString) {
    assertTrue(queryString instanceof QueryString);
    return "queryString";
  }

  @GET("/queryStringOptional")
  public String queryStringOptional(Optional<QueryString> queryString) {
    queryString.ifPresent(it -> assertTrue(it instanceof QueryString));
    return "queryStringOptional:" + queryString.isPresent();
  }

  @GET("/formdata")
  public String formdata(Formdata value) {
    assertTrue(value instanceof Formdata);
    return "formdata";
  }

  @GET("/multipart")
  public String multipart(Formdata value) {
    assertTrue(value instanceof Formdata);
    return "multipart";
  }

  @GET("/flashMap")
  public String flashMap(FlashMap value) {
    assertTrue(value instanceof FlashMap);
    return "flashMap";
  }

  @GET("/session")
  public String session(Session value) {
    assertTrue(value instanceof Session);
    return "session";
  }

  @GET("/sessionOrNull")
  public String sessionOrNull(Optional<Session> session) {
    return "session:" + session.isPresent();
  }

  @GET("/pathParam/{p1}")
  public String pathParam(@PathParam String p1) {
    return p1;
  }

  @GET("/bytePathParam/{p1}")
  public String bytePathParam(@PathParam byte p1) {
    return Integer.toString(p1);
  }

  @GET("/intPathParam/{p1}")
  public String intPathParam(@PathParam int p1) {
    return Integer.toString(p1);
  }

  @GET("/longPathParam/{p1}")
  public String longPathParam(@PathParam long p1) {
    return Long.toString(p1);
  }

  @GET("/floatPathParam/{p1}")
  public String floatPathParam(@PathParam float p1) {
    return Float.toString(p1);
  }

  @GET("/doublePathParam/{p1}")
  public String doublePathParam(@PathParam double p1) {
    return Double.toString(p1);
  }

  @GET("/booleanPathParam/{p1}")
  public String booleanPathParam(@PathParam boolean p1) {
    return Boolean.toString(p1);
  }

  @GET("/optionalStringPathParam/{p1}")
  public String optionalStringPathParam(@PathParam Optional<String> p1) {
    return p1.toString();
  }

  @GET("/optionalIntPathParam/{p1}")
  public String optionalIntPathParam(@PathParam Optional<Integer> p1) {
    return p1.toString();
  }

  @GET("/javaBeanPathParam/{foo}")
  public String javaBeanPathParam(@PathParam JavaBeanParam param) {
    return param.toString();
  }

  @GET("/listStringPathParam/{values}")
  public String listStringPathParam(@PathParam List<String> values) {
    return values.toString();
  }

  @GET("/listDoublePathParam/{values}")
  public String listDoublePathParam(@PathParam List<Double> values) {
    return values.toString();
  }

  @GET("/listBeanPathParam/{foo}")
  public String listBeanPathParam(@PathParam List<JavaBeanParam> bean) {
    return bean.toString();
  }

  @GET("/setStringPathParam/{values}")
  public String setStringPathParam(@PathParam Set<String> values) {
    return values.toString();
  }

  @GET("/setDoublePathParam/{values}")
  public String setDoublePathParam(@PathParam Set<Double> values) {
    return values.toString();
  }

  @GET("/setBeanPathParam/{foo}")
  public String setBeanPathParam(@PathParam Set<JavaBeanParam> bean) {
    return bean.toString();
  }

  @GET("/enumParam/{letter}")
  public String enumParam(@PathParam EnumParam letter) {
    return letter.name();
  }

  @GET("/optionalEnumParam/{letter}")
  public String optionalEnumParam(@PathParam Optional<EnumParam> letter) {
    return letter.toString();
  }

  @GET("/listEnumParam/{letter}")
  public String listEnumParam(@PathParam List<EnumParam> letter) {
    return letter.toString();
  }

  @GET("/primitiveWrapper/{value}")
  public String primitiveWrapper(@PathParam Integer value) {
    return String.valueOf(value);
  }

  @GET("/queryParam")
  public String queryParam(@QueryParam String q) {
    return q;
  }

  @GET("/cookieParam")
  public String cookieParam(@CookieParam String c) {
    return c;
  }

  @GET("/headerParam")
  public String headerParam(@HeaderParam Instant instant) {
    return String.valueOf(instant.toEpochMilli());
  }

  @GET("/flashParam")
  public String flashParam(@FlashParam String message) {
    return message;
  }

  @GET("/formParam")
  public String formParam(@FormParam String name) {
    return name;
  }

  @GET("/parameters/{path}")
  public String parameters(
      @PathParam String path,
      Context ctx,
      @QueryParam int offset,
      @QueryParam JavaBeanParam javaBean) {
    return path + ctx + offset + javaBean;
  }

  @GET("/fileParam")
  public String fileParam(FileUpload file) {
    return file.toString();
  }

  @GET("/fileParams")
  public String fileParams(List<FileUpload> file) {
    return file.toString();
  }

  @GET("/uuidParam")
  public String uuidParam(@QueryParam UUID value) {
    return value.toString();
  }

  @GET("/bigDecimalParam")
  public String bigDecimalParam(@QueryParam BigDecimal value) {
    return value.toString();
  }

  @GET("/bigIntegerParam")
  public String bigIntegerParam(@QueryParam BigInteger value) {
    return value.toString();
  }

  @GET("/charsetParam")
  public String charsetParam(@QueryParam Charset value) {
    return value.toString();
  }

  @GET("/pathFormParam")
  public String pathFormParam(@FormParam java.nio.file.Path file) {
    return file.toString();
  }

  @GET("/returnByte")
  public byte returnByte() {
    return 8;
  }

  @GET("/returnShort")
  public short returnShort() {
    return 8;
  }

  @GET("/returnInteger")
  public int returnInteger() {
    return 7;
  }

  @GET("/returnLong")
  public long returnLong() {
    return 9;
  }

  @GET("/returnFloat")
  public float returnFloat() {
    return 7.9f;
  }

  @GET("/returnChar")
  public char returnChar() {
    return 'c';
  }

  @GET("/returnDouble")
  public double returnDouble() {
    return 8.9;
  }

  @GET("/returnStatusCode")
  public StatusCode returnStatusCode() {
    return StatusCode.NO_CONTENT;
  }

  @GET("/statusCode")
  public StatusCode statusCode(@QueryParam StatusCode statusCode, @QueryParam String q) {
    return statusCode;
  }

  @DELETE("/noContent")
  public void noContent() {}

  @GET("/sideEffect")
  public void sideEffect(Context ctx) {
    ctx.send(StatusCode.CREATED);
  }

  @POST
  @Path("/bodyStringParamNullable")
  public String bodyStringParamNullable(String body) {
    return body;
  }

  @POST
  @Path("/bodyStringParam")
  public String bodyStringParam(@NonNull String body) {
    return body;
  }

  @POST
  @Path("/bodyBytesParam")
  public String bodyBytesParam(byte[] body) {
    return new String(body, StandardCharsets.UTF_8);
  }

  @POST
  @Path("/bodyInputStreamParam")
  public String bodyInputStreamParam(InputStream body) {
    assertTrue(body instanceof InputStream);
    return body.toString();
  }

  @POST
  @Path("/bodyChannelParam")
  public String bodyChannelParam(ReadableByteChannel body) {
    assertTrue(body instanceof ReadableByteChannel);
    return body.toString();
  }

  @POST
  @Path("/bodyBeanParam")
  public String bodyBeanParam(JavaBeanParam body) {
    return body.toString();
  }

  @POST
  @Path("/bodyIntParam")
  public int bodyIntParam(int body) {
    return body;
  }

  @POST
  @Path("/bodyOptionalIntParam")
  public Optional<Integer> bodyOptionalIntParam(Optional<Integer> body) {
    return body;
  }

  @POST
  @Path("/bodyMapParam")
  public Map<String, Object> bodyMapParam(Map<String, Object> json) {
    return json;
  }

  @POST
  @Path("/bodyCustomGenericParam")
  public CustomGenericType<String> bodyCustomGenericParam(CustomGenericType<String> body) {
    return body;
  }
}
