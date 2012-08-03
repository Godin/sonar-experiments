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

import java.util.HashMap;
import java.util.Map;

/**
 * Specifies how to transform a class.
 */
public final class ClassTransformSpec {

  private final String name;
  private final Map<String, MethodTransformSpec> methodSpecs = new HashMap<String, MethodTransformSpec>();

  public ClassTransformSpec(Class clazz, MethodTransformSpec... methodSpecs) {
    this(clazz.getName().replace('.', '/'), methodSpecs);
  }

  public ClassTransformSpec(String name, MethodTransformSpec... methodSpecs) {
    this.name = name;
    for (MethodTransformSpec s : methodSpecs) {
      this.methodSpecs.put(s.getName() + s.getDesc(), s);
    }
  }

  public String getName() {
    return name;
  }

  public Map<String, MethodTransformSpec> getMethodSpects() {
    return methodSpecs;
  }

}
