package example6;

public class Example {

  @javax.annotation.CheckForNull
  public Integer method(int parameter) {
    if (parameter > 0) {
      return 1;
    } else {
      return null;
    }
  }

  public void test() {
    int result1 = method(1); // findbugs:NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE:Dodgy - Possible null pointer dereference due to return value of called method
    Integer result2 = method(1); // findbugs:NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE:Dodgy - Possible null pointer dereference due to return value of called method
    result2.toString(); // findbugs:NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE:Dodgy - Possible null pointer dereference due to return value of called method
    Integer result3 = method(1);
    if (result3 != null) {
      result3.toString();
    }
  }

}
