/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.internal.apt.asm;

import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import io.jooby.internal.apt.JoobyTypes;
import io.jooby.internal.apt.MethodDescriptor;
import io.jooby.internal.apt.ParamDefinition;
import io.jooby.internal.apt.ParamKind;

public class ObjectTypeWriter implements ParamWriter {
  @Override
  public void accept(
      ClassWriter writer,
      Type controller,
      String handlerInternalName,
      MethodVisitor visitor,
      ParamDefinition parameter,
      NameGenerator nameGenerator)
      throws Exception {
    if (!parameter.is(JoobyTypes.Context.getClassName())) {
      MethodDescriptor method = ParamKind.forTypeInjection(parameter).valueObject(parameter);
      visitor.visitMethodInsn(
          INVOKEINTERFACE,
          method.getDeclaringType().getInternalName(),
          method.getName(),
          method.getDescriptor(),
          true);
    }
    if (parameter.isOptional()) {
      visitor.visitMethodInsn(
          INVOKESTATIC,
          "java/util/Optional",
          "ofNullable",
          "(Ljava/lang/Object;)Ljava/util/Optional;",
          false);
    }
  }
}
