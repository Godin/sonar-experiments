package example;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @goal sayhi
 * @requiresProject false
 */
public class GreetingMojo extends AbstractMojo
{
    public void execute() throws MojoExecutionException
    {
        coveredByIntegrationTest();
    }

    public void coveredByUnitTest()
    {
       System.out.println("Hello, world.");
    }

    public void coveredByIntegrationTest()
    {
       getLog().info("Hello, world.");
    }
}
