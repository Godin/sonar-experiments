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

import org.sonar.plugins.monitor.agent.Listener;

/**
 * Intercepts the {@code void close()} method and calls {@link Listener#close(Object)} in the end.
 */
public class CloseInterceptor extends MethodAppender {
  public CloseInterceptor() {
    this("close");
  }

  public CloseInterceptor(String methodName) {
    super(methodName, "()V");
  }

  protected void append(CodeGenerator g) {
    g.invokeAppStatic(Listener.class, "close",
        new Class[] {Object.class},
        new int[] {0});
  }
}
