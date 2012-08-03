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

import org.objectweb.asm.MethodVisitor;
import org.sonar.plugins.monitor.agent.Listener;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Intercepts the this.open(...) call in the constructor.
 */
public class ConstructorOpenInterceptor extends MethodAppender {

  /**
   * Binary name of the class being transformed.
   */
  private final String binName;

  public ConstructorOpenInterceptor(String constructorDesc, String binName) {
    super("<init>", constructorDesc);
    this.binName = binName;
  }

  @Override
  public MethodVisitor newAdapter(MethodVisitor base, int access, String name, String desc, String signature, String[] exceptions) {
    final MethodVisitor b = super.newAdapter(base, access, name, desc, signature, exceptions);
    return new OpenInterceptionAdapter(b, access, desc) {
      @Override
      protected boolean toIntercept(String owner, String name) {
        return owner.equals(binName) && name.startsWith("open");
      }

      @Override
      protected Class<? extends Exception> getExpectedException() {
        return FileNotFoundException.class;
      }
    };
  }

  protected void append(CodeGenerator g) {
    g.invokeAppStatic(Listener.class, "open",
        new Class[] {Object.class, File.class},
        new int[] {0, 1});
  }

}
