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
package org.sonar.plugins.monitor;

import org.sonar.plugins.monitor.agent.AgentMain;
import org.sonar.plugins.monitor.agent.FileRecord;
import org.sonar.plugins.monitor.agent.Snapshot;
import org.sonar.plugins.monitor.agent.VirtualMachineUtils;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class Main {

  public static void main(String[] args) throws Exception {
    System.out.println(FileRecord.class.getName());

    VirtualMachineDescriptor vmd = VirtualMachineUtils.getCurrentVM();
    System.out.println("Current VM: " + vmd.id() + " " + vmd.displayName());
    VirtualMachine vm = null;
    File file = createTemporaryAgentJar();
    try {
      vm = VirtualMachine.attach(vmd);
      vm.loadAgent(file.getAbsolutePath());
    } finally {
      VirtualMachineUtils.detachQuetly(vm);
      file.delete();
    }

    Snapshot before = Snapshot.takeNow();

    file = File.createTempFile("test", ".test");
    FileOutputStream fos = new FileOutputStream(file);
    Snapshot.takeNow().diffSince(before);
    fos.close();
    file.delete();
  }

  private static File createTemporaryAgentJar() throws FileNotFoundException, IOException {
    Manifest manifest = new Manifest();
    manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
    manifest.getMainAttributes().put(new Attributes.Name("Agent-Class"), AgentMain.class.getName());
    manifest.getMainAttributes().put(new Attributes.Name("Can-Retransform-Classes"), "true"); // important for Oracle JVM 1.7
    File file = File.createTempFile("agent", ".jar");
    JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(file), manifest);
    jarOutputStream.close();
    return file;
  }

}
