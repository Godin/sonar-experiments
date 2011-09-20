This example demonstrates how to collect code coverage by tests, which executed by [maven-invoker-plugin](http://maven.apache.org/plugins/maven-invoker-plugin/).

1.  Build project and execute all tests:

        mvn clean install

2.  Analyse by Sonar :

        mvn sonar:sonar
