package example4;

public class Example {

  public void method(@javax.annotation.Nullable String s) {
    System.out.println(s.toString()); // findbugs:NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE:Dodgy - Parameter must be nonnull but is marked as nullable
  }

  public void test() {
    method("foo");
  }

}
