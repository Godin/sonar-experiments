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
import java.io.FileInputStream;
import java.util.Map;
import java.util.WeakHashMap;

public class Listener {

  /**
   * Files that are currently open, keyed by the owner object (like {@link FileInputStream}.
   */
  public static Map<Object, FileRecord> TABLE = new WeakHashMap<Object, FileRecord>();

  public static synchronized void open(Object _this, File file) {
    FileRecord r = new FileRecord(file);
    TABLE.put(_this, r);
  }

  public static synchronized void close(Object _this) {
    TABLE.remove(_this);
  }

  public static synchronized void outOfDescriptors() {
    // TODO
  }

}
