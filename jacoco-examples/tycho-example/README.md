This example demonstrates how to collect code coverage by tests, which executed by [tycho-surefire-plugin](http://www.eclipse.org/tycho/).

1.  Build project and execute all tests:

        mvn clean install

2.  Analyse by Sonar :

        mvn sonar:sonar
