import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLClassLoader;

import javax.management.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class ExampleTest {

  @Test
  public void test() throws Exception {
    printOpenFileDescriptorCount();

    String iterationsProperty = System.getProperty("iters");
    int iterations = iterationsProperty == null ? 1024 : Integer.parseInt(iterationsProperty);

    File workDir;
    String workDirProperty = System.getProperty("workdir");
    workDir = workDirProperty == null ? new File("./target/work") : new File(workDirProperty);
    System.out.println("Work dir: " + workDir);
    FileUtils.deleteDirectory(workDir);
    workDir.mkdirs();

    // some jar-file with a content
    File origin = new File(getClass().getClassLoader().loadClass("org.junit.Test").getProtectionDomain().getCodeSource().getLocation().getPath());

    for (int i = 0; i < iterations; i++) {
      printOpenFileDescriptorCount();

      File dest = new File(workDir, i + ".jar");
      copyFile(origin, dest);

      ClassLoader cl = new URLClassLoader(
          new URL[] { dest.toURL() },
          null // we don't use parent ClassLoader to enforce loading from newly created jar-file
      );

      InputStream stream = cl.getResourceAsStream("org/junit/Test.class");
      IOUtils.closeQuietly(stream);
    }
  }

  private static void copyFile(File sourceFile, File destFile) throws IOException {
    InputStream in = new FileInputStream(sourceFile);
    OutputStream out = new FileOutputStream(destFile);
    byte[] buf = new byte[1024];
    int len;
    while ((len = in.read(buf)) > 0) {
      out.write(buf, 0, len);
    }
    IOUtils.closeQuietly(in);
    IOUtils.closeQuietly(out);
  }

  private static void printOpenFileDescriptorCount() {
    try {
      ObjectName oName = new ObjectName("java.lang:type=OperatingSystem");
      MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
      AttributeList list = mbs.getAttributes(oName, new String[] { "OpenFileDescriptorCount" });
      for (int i = 0; i < list.size(); i++) {
        Attribute attr = (Attribute) list.get(i);
        System.out.println(attr.getName() + " -> " + attr.getValue());
      }
    } catch (JMException e) {
      e.printStackTrace();
    }
  }

}
