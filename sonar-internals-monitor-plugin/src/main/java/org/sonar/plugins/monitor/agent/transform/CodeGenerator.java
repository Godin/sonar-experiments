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

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.POP;

/**
 * Convenience methods to generate bytecode.
 */
public class CodeGenerator extends MethodAdapter {

  public CodeGenerator(MethodVisitor mv) {
    super(mv);
  }

  public void println(String msg) {
    super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
    ldc(msg);
    super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
  }

  public void _null() {
    super.visitInsn(ACONST_NULL);
  }

  public void newArray(String type, int size) {
    iconst(size);
    super.visitTypeInsn(ANEWARRAY, type);
  }

  public void iconst(int i) {
    if (i <= 5)
      super.visitInsn(ICONST_0 + i);
    else super.visitLdcInsn(i);
  }

  public void dup() {
    super.visitInsn(DUP);
  }

  public void aastore() {
    super.visitInsn(AASTORE);
  }

  public void aload(int i) {
    super.visitIntInsn(ALOAD, i);
  }

  public void pop() {
    super.visitInsn(POP);
  }

  public void ldc(Object o) {
    if (o.getClass() == Class.class)
      o = Type.getType((Class) o);
    super.visitLdcInsn(o);
  }

  public void invokeVirtual(String owner, String name, String desc) {
    super.visitMethodInsn(INVOKEVIRTUAL, owner, name, desc);
  }

  /**
   * Invokes a static method on the class in the system classloader.
   *
   * This is used for instrumenting classes in the bootstrap classloader,
   * which cannot see the classes in the system classloader.
   */
  public void invokeAppStatic(Class userClass, String userMethodName, Class[] argTypes, int[] localIndex) {
    invokeAppStatic(userClass.getName(), userMethodName, argTypes, localIndex);
  }

  public void invokeAppStatic(String userClassName, String userMethodName, Class[] argTypes, int[] localIndex) {
    Label s = new Label();
    Label e = new Label();
    Label h = new Label();
    Label tail = new Label();
    visitTryCatchBlock(s, e, h, "java/lang/Exception");
    visitLabel(s);
    // [RESULT] m = ClassLoader.getSystemClassLoadeR().loadClass($userClassName).getDeclaredMethod($userMethodName,[...]);
    visitMethodInsn(INVOKESTATIC, "java/lang/ClassLoader", "getSystemClassLoader", "()Ljava/lang/ClassLoader;");
    ldc(userClassName);
    invokeVirtual("java/lang/ClassLoader", "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");
    ldc(userMethodName);
    newArray("java/lang/Class", argTypes.length);
    for (int i = 0; i < argTypes.length; i++)
      storeConst(i, argTypes[i]);

    invokeVirtual("java/lang/Class", "getDeclaredMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;");

    // [RESULT] m.invoke(null,new Object[]{this,file})
    _null();
    newArray("java/lang/Object", argTypes.length);

    for (int i = 0; i < localIndex.length; i++) {
      dup();
      iconst(i);
      aload(localIndex[i]);
      aastore();
    }

    visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
    pop();
    _goto(tail);

    visitLabel(e);
    visitLabel(h);

    // [RESULT] catch(e) { e.printStackTrace(System.out); }
    visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
    invokeVirtual("java/lang/Exception", "printStackTrace", "(Ljava/io/PrintStream;)V");

    visitLabel(tail);
  }

  /**
   * When the stack top is an array, store a constant to the known index of the array.
   */
  private void storeConst(int idx, Object type) {
    dup();
    iconst(idx);
    ldc(type);
    aastore();
  }

  public void _goto(Label l) {
    visitJumpInsn(GOTO, l);
  }

  public void ifFalse(Label label) {
    visitJumpInsn(IFEQ, label);
  }

  public void athrow() {
    visitInsn(ATHROW);
  }

}
