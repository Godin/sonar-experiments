package example7;

public class Example {

  @javax.annotation.Nonnull
  public String method() {
    return null; // findbugs:NP_NONNULL_RETURN_VIOLATION:Correctness - Method may return null, but is declared @NonNull
  }

  public void test() {
    System.out.println(method().toString());
  }

}
