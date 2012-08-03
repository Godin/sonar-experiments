/*
 * Sonar Internals Monitor Plugin
 * Copyright (C) 2012 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.monitor.agent.transform;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.sonar.plugins.monitor.agent.Listener;

import java.io.IOException;

/**
 * Rewrites a method that includes a call to a native method that actually opens a file descriptor
 * (therefore it can throw "too many open files" exception.)
 *
 * surround the call with try/catch, and if "too many open files" exception is thrown
 * call {@link Listener#outOfDescriptors()}.
 */
public abstract class OpenInterceptionAdapter extends MethodAdapter {

  private final LocalVariablesSorter lvs;
  private final MethodVisitor base;

  public OpenInterceptionAdapter(MethodVisitor base, int access, String desc) {
    super(null);
    lvs = new LocalVariablesSorter(access, desc, base);
    mv = lvs;
    this.base = base;
  }

  /**
   * Decide if this is the method that needs interception.
   */
  protected abstract boolean toIntercept(String owner, String name);

  protected Class<? extends Exception> getExpectedException() {
    return IOException.class;
  }

  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc) {
    if (!toIntercept(owner, name)) {
      // no processing
      super.visitMethodInsn(opcode, owner, name, desc);
      return;
    }
    Type exceptionType = Type.getType(getExpectedException());

    CodeGenerator g = new CodeGenerator(mv);
    Label s = new Label(); // start of the try block
    Label e = new Label(); // end of the try block
    Label h = new Label(); // handler entry point
    Label tail = new Label(); // where the execution continue

    g.visitTryCatchBlock(s, e, h, exceptionType.getInternalName());
    g.visitLabel(s);
    super.visitMethodInsn(opcode, owner, name, desc);
    g._goto(tail);

    g.visitLabel(e);
    g.visitLabel(h);
    // [RESULT]
    // catch(E ex) {
    // boolean b = ex.getMessage().contains("Too many open files");
    int ex = lvs.newLocal(exceptionType);
    g.dup();
    base.visitVarInsn(Opcodes.ASTORE, ex);
    g.invokeVirtual(exceptionType.getInternalName(), "getMessage", "()Ljava/lang/String;");
    g.ldc("Too many open files");
    g.invokeVirtual("java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z");

    // too many open files detected
    // if (b) { Listener.outOfDescriptors() }
    Label rethrow = new Label();
    g.ifFalse(rethrow);

    g.invokeAppStatic(Listener.class, "outOfDescriptors",
        new Class[0], new int[0]);

    // rethrow the FileNotFoundException
    g.visitLabel(rethrow);
    base.visitVarInsn(Opcodes.ALOAD, ex);
    g.athrow();

    // normal execution continues here
    g.visitLabel(tail);
  }

}
