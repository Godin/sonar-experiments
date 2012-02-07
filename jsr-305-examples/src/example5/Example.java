package example5;

public class Example {

  @javax.annotation.Nonnull
  private String s = "foo";

  public void test() {
    s = null; // findbugs:NP_STORE_INTO_NONNULL_FIELD:Correctness - Store of null value into field annotated NonNull
  }

}
