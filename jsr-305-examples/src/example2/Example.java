package example2;

public class Example {

  public void method(@javax.annotation.Nonnull String s) {
  }

  public void test() {
    method(null); // findbugs:NP_NONNULL_PARAM_VIOLATION:Correctness - Method call passes null to a nonnull parameter
  }

}
