package example;

import org.junit.Test;

public class GreetingMojoTest
{
    @Test
    public void test() {
      new GreetingMojo().coveredByUnitTest();
    }
}
