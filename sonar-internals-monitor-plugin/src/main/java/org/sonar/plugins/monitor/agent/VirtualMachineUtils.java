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

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.IOException;

public final class VirtualMachineUtils {

  private VirtualMachineUtils() {
  }

  public static VirtualMachineDescriptor getCurrentVM() {
    VirtualMachineDescriptor result = null;
    String id = VirtualMachineUtils.class.getName() + System.currentTimeMillis();
    System.setProperty(id, id);
    for (VirtualMachineDescriptor vmd : VirtualMachine.list()) {
      if (id.equals(getSystemPropertyFromVM(vmd, id))) {
        result = vmd;
        break;
      }
    }
    System.getProperties().remove(id);
    return result;
  }

  public static String getSystemPropertyFromVM(VirtualMachineDescriptor vmd, String propertyName) {
    VirtualMachine vm = null;
    try {
      vm = VirtualMachine.attach(vmd);
      return vm.getSystemProperties().getProperty(propertyName);
    } catch (Exception e) {
      return null;
    } finally {
      detachQuetly(vm);
    }
  }

  public static void detachQuetly(VirtualMachine vm) {
    if (vm != null) {
      try {
        vm.detach();
      } catch (IOException e) {
        // ignore
      }
    }
  }

}
