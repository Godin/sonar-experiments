package example6;

public class Example {

  @javax.annotation.CheckForNull
  public String method() {
    return null;
  }

  public void test() {
    System.out.println(method().toString()); //findbugs:NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE:Dodgy - Possible null pointer dereference due to return value of called method
  }

}
