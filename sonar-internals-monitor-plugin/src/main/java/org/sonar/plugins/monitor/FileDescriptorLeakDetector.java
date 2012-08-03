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

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.events.DecoratorExecutionHandler;
import org.sonar.api.batch.events.SensorExecutionHandler;
import org.sonar.api.resources.Project;
import org.sonar.plugins.monitor.agent.AttachApiUtils;
import org.sonar.plugins.monitor.agent.Listener;
import org.sonar.plugins.monitor.agent.Snapshot;
import org.sonar.plugins.monitor.agent.VirtualMachineUtils;

import javax.management.ObjectName;

import java.io.File;
import java.lang.management.ManagementFactory;

/**
 * Intercepts execution of sensors and decorators and compares number of open file descriptors in order to report about leaks.
 */
public class FileDescriptorLeakDetector implements SensorExecutionHandler, DecoratorExecutionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(FileDescriptorLeakDetector.class);

  private static boolean agentInstalled = false;

  private long before;

  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  public void onSensorExecution(SensorExecutionEvent event) {
    if (!agentInstalled) {
      installAgent();
      agentInstalled = true;
    }
    detect(event.isEnd(), event.getSensor().getClass().getName());
  }

  private void installAgent() {
    AttachApiUtils.loadAttachApi();

    VirtualMachineDescriptor vmd = VirtualMachineUtils.getCurrentVM();
    VirtualMachine vm = null;
    try {
      vm = VirtualMachine.attach(vmd);
      File agent = AttachApiUtils.whichJar(getClass());
      System.out.println("Agent: " + agent);
      vm.loadAgent(agent.getAbsolutePath());
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      VirtualMachineUtils.detachQuetly(vm);
    }
  }

  public void onDecoratorExecution(DecoratorExecutionEvent event) {
    detect(event.isEnd(), event.getDecorator().getClass().getName());
  }

  private Snapshot previousSnapshot;

  private void detect(boolean shouldReport, String eventDescription) {
    long current = Listener.TABLE.size();
    if (shouldReport) {
      if (current > before) {
        LOG.warn("File descriptor leak detected during execution of " + eventDescription + " (before=" + before + ",after=" + current + ",leaked=" + (current - before) + ") ");
        Snapshot.takeNow().diffSince(previousSnapshot);
      }
    } else {
      previousSnapshot = Snapshot.takeNow();
    }
    before = current;
  }

  private static long getOpenFileDescriptorCount() {
    try {
      ObjectName name = new ObjectName(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME);
      // Available only on Unix
      return (Long) ManagementFactory.getPlatformMBeanServer().getAttribute(name, "OpenFileDescriptorCount");
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return -1;
    }
  }

}
