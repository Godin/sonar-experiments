package example1;

public class Example {

  public void method(String s) {
    System.out.println(s.toString());
  }

  public void test() {
    method(null); // findbugs:NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS:Correctness - Method call passes null for nonnull parameter (ALL_TARGETS_DANGEROUS)
  }

}
