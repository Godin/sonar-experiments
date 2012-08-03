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
package org.sonar.plugins.monitor.agent;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public final class AttachApiUtils {

  private AttachApiUtils() {
  }

  /**
   * Finds the jar file from a reference to class within.
   */
  public static File whichJar(Class c) {
    String url = c.getClassLoader().getResource(c.getName().replace('.', '/') + ".class").toExternalForm();
    if (url.startsWith("jar:file:")) {
      url = url.substring(0, url.lastIndexOf('!'));
      url = url.substring(9);
      return new File(url);
    }
    throw new IllegalStateException("Unable to figure out the file of the jar: " + url);
  }

  /**
   * Loads the {@link com.sun.tools.attach.VirtualMachine} class as the entry point to the attach API.
   */
  public static Class loadAttachApi() {
    File toolsJar = locateToolsJar();
    ClassLoader cl = wrapIntoClassLoader(toolsJar);
    try {
      return cl.loadClass("com.sun.tools.attach.VirtualMachine");
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("Unable to find com.sun.tools.attach.VirtualMachine", e);
    }
  }

  private static ClassLoader wrapIntoClassLoader(File toolsJar) {
    try {
      URL jar = toolsJar.toURI().toURL();
      ClassLoader base = ClassLoader.getSystemClassLoader();
      Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
      addURL.setAccessible(true);
      addURL.invoke(base, jar);
      return base;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Locates the {@code tools.jar} file.
   */
  private static File locateToolsJar() {
    File home = new File(System.getProperty("java.home"));
    return new File(home, "../lib/tools.jar");
  }

}
