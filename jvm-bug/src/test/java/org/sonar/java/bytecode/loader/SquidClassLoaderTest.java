package org.sonar.java.bytecode.loader;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

public class SquidClassLoaderTest {

  private File getTempFile() throws Exception {
    // some jar-file with a content
    File srcFile = new File(getClass().getClassLoader().loadClass("org.junit.Test").getProtectionDomain().getCodeSource().getLocation().getPath());
    File tempFile = File.createTempFile("test", "jar");
    FileUtils.copyFile(srcFile, tempFile);

    System.out.println(tempFile);

    return tempFile;
  }

  @Ignore
  @Test
  public void testSquidClassLoader() throws Exception {
    File file = getTempFile();
    SquidClassLoader classLoader = new SquidClassLoader(Collections.singleton(file));
    InputStream is = classLoader.getResourceAsStream("org/junit/Test.class");
    IOUtils.closeQuietly(is);
    classLoader.close();
    file.delete();
  }

  @Test
  public void testUrlClassLoader() throws Exception {
    File file = getTempFile();
    URLClassLoader classLoader = new URLClassLoader(new URL[] { file.toURI().toURL() });
    System.out.println(classLoader.loadClass("org.junit.Test"));
    InputStream is = classLoader.getResourceAsStream("org/junit/Test.class");
    System.out.println(is);
    // file.delete();
    IOUtils.closeQuietly(is);
  }

}
