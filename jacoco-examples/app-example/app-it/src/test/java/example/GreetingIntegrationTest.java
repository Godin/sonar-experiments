package example;

import org.junit.Test;

public class GreetingIntegrationTest
{
    @Test
    public void test()
    {
      new Greeting().coveredByIntegrationTest();
    }
}
