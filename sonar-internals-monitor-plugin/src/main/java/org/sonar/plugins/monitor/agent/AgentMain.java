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

import org.sonar.plugins.monitor.agent.transform.CloseInterceptor;
import org.sonar.plugins.monitor.agent.transform.ConstructorOpenInterceptor;

import org.sonar.plugins.monitor.agent.transform.ClassTransformSpec;
import org.sonar.plugins.monitor.agent.transform.Transformer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipFile;

public class AgentMain {

  public static void agentmain(String agentArguments, Instrumentation instrumentation) throws Exception {
    install(instrumentation);
  }

  public static void premain(String agentArguments, Instrumentation instrumentation) throws Exception {
    install(instrumentation);
  }

  private static void install(Instrumentation instrumentation) throws Exception {
    instrumentation.addTransformer(new Transformer(createSpec()), true);
    instrumentation.retransformClasses(
        FileInputStream.class,
        FileOutputStream.class,
        RandomAccessFile.class,
        ZipFile.class);
  }

  private static List<ClassTransformSpec> createSpec() {
    return Arrays.asList(
        newSpec(FileOutputStream.class, "(Ljava/io/File;Z)V"),
        newSpec(FileInputStream.class, "(Ljava/io/File;)V"),
        newSpec(RandomAccessFile.class, "(Ljava/io/File;Ljava/lang/String;)V"),
        newSpec(ZipFile.class, "(Ljava/io/File;I)V"));
  }

  private static ClassTransformSpec newSpec(Class c, String constructorDesc) {
    String binName = c.getName().replace('.', '/');
    return new ClassTransformSpec(binName,
        new ConstructorOpenInterceptor(constructorDesc, binName),
        new CloseInterceptor());
  }

}
