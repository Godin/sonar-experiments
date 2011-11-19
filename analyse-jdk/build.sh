#!/bin/sh -e

rm -rf src target
mkdir -p src target/classes
unzip src.zip -d src
unzip rt.jar -d target/classes

export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=128m"
mvn -V \
    -Denforcer.skip=true \
    -Dsonar.database=postgresql -Dsonar.jdbc.driver=org.postgresql.Driver -Dsonar.jdbc.url=jdbc:postgresql://localhost/sonar \
    sonar:sonar $*
