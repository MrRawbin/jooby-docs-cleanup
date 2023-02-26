/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.internal.apt;

import static org.objectweb.asm.Type.BOOLEAN_TYPE;
import static org.objectweb.asm.Type.BYTE_TYPE;
import static org.objectweb.asm.Type.CHAR_TYPE;
import static org.objectweb.asm.Type.DOUBLE_TYPE;
import static org.objectweb.asm.Type.FLOAT_TYPE;
import static org.objectweb.asm.Type.INT_TYPE;
import static org.objectweb.asm.Type.LONG_TYPE;
import static org.objectweb.asm.Type.SHORT_TYPE;
import static org.objectweb.asm.Type.VOID_TYPE;
import static org.objectweb.asm.Type.getObjectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import org.objectweb.asm.Type;

public class TypeDefinition {

  private final Types typeUtils;
  private final TypeMirror type;

  public TypeDefinition(Types types, TypeMirror type) {
    this.typeUtils = types;
    this.type = type;
  }

  /**
   * Check for declared type and get the underlying type. This is required for annotated type.
   * Example:
   *
   * <pre>{@code
   * @Nullable @QueryParam String name
   * }</pre>
   *
   * @param type
   * @return
   */
  private TypeMirror unwrapType(TypeMirror type) {
    if (type instanceof DeclaredType) {
      return ((DeclaredType) type).asElement().asType();
    } else {
      return type;
    }
  }

  public String getName() {
    return getRawType().toString();
  }

  public TypeMirror getType() {
    return type;
  }

  public boolean isPrimitive() {
    return unwrapType(getType()).getKind().isPrimitive();
  }

  public boolean isVoid() {
    return unwrapType(getType()).getKind() == TypeKind.VOID;
  }

  public TypeMirror getRawType() {
    return typeUtils.erasure(unwrapType(getType()));
  }

  public boolean is(Class type, Class... arguments) {
    return is(typeName(type), Stream.of(arguments).map(this::typeName).toArray(String[]::new));
  }

  public boolean is(String type, String... arguments) {
    if (!equalType(getType(), type)) {
      return false;
    }
    if (arguments.length > 0 && this.type instanceof DeclaredType) {
      DeclaredType declaredType = (DeclaredType) this.type;
      List<? extends TypeMirror> args = declaredType.getTypeArguments();
      if (args.size() != arguments.length) {
        return false;
      }
      for (int i = 0; i < arguments.length; i++) {
        if (!equalType(args.get(i), arguments[i])) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean equalType(TypeMirror type, String typeName) {
    TypeMirror realType = unwrapType(type);
    TypeMirror erasure = typeUtils.erasure(realType);
    if (!erasure.toString().equals(typeName)) {
      // check for enum subclasses:
      if (Enum.class.getName().equals(typeName)) {
        return typeUtils.asElement(realType).getKind() == ElementKind.ENUM;
      } else {
        return false;
      }
    }
    return true;
  }

  public boolean isParameterizedType() {
    if (type instanceof DeclaredType) {
      DeclaredType declaredType = (DeclaredType) type;
      return declaredType.getTypeArguments().size() > 0;
    }
    return false;
  }

  public List<TypeDefinition> getArguments() {
    if (type instanceof DeclaredType) {
      DeclaredType declaredType = (DeclaredType) type;
      List<TypeDefinition> result = new ArrayList<>();
      for (TypeMirror typeArgument : declaredType.getTypeArguments()) {
        result.add(new TypeDefinition(typeUtils, typeArgument));
      }
      return result;
    }
    return Collections.emptyList();
  }

  public Type toJvmType() {
    return asmType(getName(typeUtils.erasure(type)));
  }

  public boolean isRawType() {
    return type.toString().equals(getRawType().toString());
  }

  @Override
  public String toString() {
    return type.toString();
  }

  private org.objectweb.asm.Type asmType(String type) {
    switch (type) {
      case "byte":
        return BYTE_TYPE;
      case "byte[]":
        return org.objectweb.asm.Type.getType(byte[].class);
      case "int":
        return INT_TYPE;
      case "int[]":
        return org.objectweb.asm.Type.getType(int[].class);
      case "long":
        return LONG_TYPE;
      case "long[]":
        return org.objectweb.asm.Type.getType(long[].class);
      case "float":
        return FLOAT_TYPE;
      case "float[]":
        return org.objectweb.asm.Type.getType(float[].class);
      case "double":
        return DOUBLE_TYPE;
      case "double[]":
        return org.objectweb.asm.Type.getType(double[].class);
      case "boolean":
        return BOOLEAN_TYPE;
      case "boolean[]":
        return org.objectweb.asm.Type.getType(boolean[].class);
      case "void":
        return VOID_TYPE;
      case "short":
        return SHORT_TYPE;
      case "short[]":
        return org.objectweb.asm.Type.getType(short[].class);
      case "char":
        return CHAR_TYPE;
      case "char[]":
        return org.objectweb.asm.Type.getType(char[].class);
      case "java.lang.String":
        return org.objectweb.asm.Type.getType(String.class);
      case "java.lang.String[]":
        return org.objectweb.asm.Type.getType(String[].class);
      default:
        StringBuilder prefix = new StringBuilder();
        String postfix = "";
        while (type.endsWith("[]")) {
          prefix.append("[");
          type = type.substring(0, type.length() - 2);
        }
        if (prefix.length() > 0) {
          prefix.append("L");
          postfix = ";";
        }
        return getObjectType(prefix + type.replace(".", "/") + postfix);
    }
  }

  private String typeName(Class type) {
    return type.isArray() ? type.getComponentType().getName() + "[]" : type.getName();
  }

  private String getName(TypeMirror type) {
    Element element = typeUtils.asElement(type);
    return element == null ? type.toString() : getName(element);
  }

  private String getName(Element type) {
    Element parent = type.getEnclosingElement();
    if (parent != null && parent.getKind() == ElementKind.CLASS) {
      return getName(parent) + "$" + type.getSimpleName();
    }
    return type.toString();
  }
}
