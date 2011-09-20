package example;

import org.junit.Test;

public class GreetingIT
{
    @Test
    public void test()
    {
      new Greeting().coveredByIntegrationTest();
    }
}
