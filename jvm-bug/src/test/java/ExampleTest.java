import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.FileChannel;

import javax.management.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class ExampleTest {

  private static int ITERATIONS = 1024;

  @Test
  public void test() throws Exception {
    printOpenFileDescriptorCount();

    File workDir = new File("./target/work");
    FileUtils.deleteDirectory(workDir);
    workDir.mkdirs();

    // some jar-file with a content
    File origin = new File(getClass().getClassLoader().loadClass("org.junit.Test").getProtectionDomain().getCodeSource().getLocation().getPath());

    for (int i = 0; i < ITERATIONS; i++) {
      printOpenFileDescriptorCount();

      File dest = new File(workDir, i + ".jar");
      copyFile(origin, dest);

      ClassLoader cl = new URLClassLoader(
          new URL[] { dest.toURL() },
          null // we don't use parent ClassLoader to enforce loading from newly created jar-file
      );
      IOUtils.closeQuietly(cl.getResourceAsStream("org/junit/Test.class"));
    }
  }

  private static void copyFile(File sourceFile, File destFile) throws IOException {
    if (!destFile.exists()) {
      destFile.createNewFile();
    }
    FileChannel source = null;
    FileChannel destination = null;
    try {
      source = new FileInputStream(sourceFile).getChannel();
      destination = new FileOutputStream(destFile).getChannel();
      destination.transferFrom(source, 0, source.size());
    } finally {
      if (source != null) {
        source.close();
      }
      if (destination != null) {
        destination.close();
      }
    }
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
