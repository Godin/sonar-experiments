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

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Transformer implements ClassFileTransformer {

  private final Map<String, ClassTransformSpec> specs = new HashMap<String, ClassTransformSpec>();

  public Transformer(Collection<ClassTransformSpec> specs) {
    for (ClassTransformSpec spec : specs) {
      this.specs.put(spec.getName(), spec);
    }
  }

  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)
      throws IllegalClassFormatException {
    return transform(className, classfileBuffer);
  }

  private byte[] transform(String className, byte[] classfileBuffer) {
    final ClassTransformSpec cs = specs.get(className);
    if (cs == null) {
      return classfileBuffer;
    }
    System.out.println("Transforming " + className);

    ClassReader cr = new ClassReader(classfileBuffer);
    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    cr.accept(new ClassAdapter(cw) {
      @Override
      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor base = super.visitMethod(access, name, desc, signature, exceptions);
        MethodTransformSpec ms = cs.getMethodSpects().get(name + desc);
        if (ms == null) {
          ms = cs.getMethodSpects().get(name + "*");
        }
        if (ms == null) {
          return base;
        }
        return ms.newAdapter(base, access, name, desc, signature, exceptions);
      }
    }, ClassReader.SKIP_FRAMES);

    return cw.toByteArray();
  }

}
