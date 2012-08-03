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

import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * {@link MethodTransformSpec} that adds some code right before the return statement.
 */
public abstract class MethodAppender extends MethodTransformSpec {

  public MethodAppender(String name, String desc) {
    super(name, desc);
  }

  @Override
  public MethodVisitor newAdapter(MethodVisitor base, int access, String name, String desc, String signature, String[] exceptions) {
    final CodeGenerator cg = new CodeGenerator(base);
    return new MethodAdapter(base) {
      @Override
      public void visitInsn(int opcode) {
        if (opcode == Opcodes.RETURN) {
          append(cg);
        }
        super.visitInsn(opcode);
      }
    };
  }

  /**
   * Generates code to be appended right before the return statement.
   */
  protected abstract void append(CodeGenerator g);

}
