/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.jooby.internal.ValueConverters;
import io.jooby.internal.converter.StringConstructorConverter;
import io.jooby.internal.converter.ValueOfConverter;

/**
 * Value converter for simple values that come from query, path, form, etc... parameters into more
 * specific type.
 *
 * <p>It is an extension point for {@link Value#to(Class)} calls.
 */
public interface ValueConverter {
  /**
   * True if the converter applies for the given type.
   *
   * @param type Conversion type.
   * @return True if the converter applies for the given type.
   */
  boolean supports(@NonNull Class type);

  /**
   * Convert simple to specific type.
   *
   * @param value Value value.
   * @param type Requested type.
   * @return Converted value.
   */
  Object convert(@NonNull Value value, @NonNull Class type);

  static List<ValueConverter> defaults() {
    return ValueConverters.defaultConverters();
  }

  static void addFallbackConverters(List<ValueConverter> input) {
    input.add(new ValueOfConverter());
    input.add(new StringConstructorConverter());
  }
}
