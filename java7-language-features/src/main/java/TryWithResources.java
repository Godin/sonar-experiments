import java.io.FileInputStream;

class TryWithResources {

  public void example() throws Exception {
    try (FileInputStream in = new FileInputStream("foo.txt")) {
      int k;
      while ((k = in.read()) != -1) {
        System.out.write(k);
      }
    }
  }

}
