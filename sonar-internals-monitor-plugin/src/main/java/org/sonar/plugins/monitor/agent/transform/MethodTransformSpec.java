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

/**
 * Specifies how to transform method.
 */
public abstract class MethodTransformSpec {

  private final String name;
  private final String desc;

  public MethodTransformSpec(String name, String desc) {
    this.name = name;
    this.desc = desc;
  }

  /**
   * Creates a visitor that receives the original method definition and writes the transformed method to the given base.
   */
  public abstract MethodVisitor newAdapter(MethodVisitor base, int access, String name, String desc, String signature, String[] exceptions);

  /**
   * @return name of the method to transform.
   */
  public String getName() {
    return name;
  }

  /**
   * @return method signature.
   */
  public String getDesc() {
    return desc;
  }

}
