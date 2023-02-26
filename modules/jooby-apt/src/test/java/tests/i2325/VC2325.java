/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package tests.i2325;

import io.jooby.Value;
import io.jooby.ValueConverter;

public class VC2325 implements ValueConverter {
  @Override
  public boolean supports(Class type) {
    return type == MyID2325.class;
  }

  @Override
  public Object convert(Value value, Class type) {
    return new MyID2325(value.value());
  }
}
