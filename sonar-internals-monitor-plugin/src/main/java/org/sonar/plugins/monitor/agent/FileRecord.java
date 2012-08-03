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
import java.io.PrintStream;
import java.util.Date;

/**
 * Record of opened file.
 */
public final class FileRecord {

  private final Exception stackTrace = new Exception();
  private final String threadName;
  private final long time;
  private final File file;

  public FileRecord(File file) {
    this.file = file;
    // keeping a Thread would potentially leak a thread, so let's just do a name
    this.threadName = Thread.currentThread().getName();
    this.time = System.currentTimeMillis();
  }

  public void dump(String prefix, PrintStream out) {
    out.println(prefix + file + " by thread:" + threadName + " on " + new Date(time));
    StackTraceElement[] trace = stackTrace.getStackTrace();
    int i = 0;
    // skip until we find the Method.invoke() that called us
    for (; i < trace.length; i++) {
      if (trace[i].getClassName().equals("java.lang.reflect.Method")) {
        i++;
        break;
      }
    }
    // print the rest
    for (; i < trace.length; i++) {
      out.println("\tat " + trace[i]);
    }
    out.flush();
  }

}
