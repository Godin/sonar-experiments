package example3;

@javax.annotation.ParametersAreNonnullByDefault
public class Example {

  public void method(String s) {
  }

  public void test() {
    method(null); // findbugs:NP_NONNULL_PARAM_VIOLATION:Correctness - Method call passes null to a nonnull parameter
  }

}
