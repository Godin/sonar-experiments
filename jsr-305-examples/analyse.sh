#!/bin/sh -e

mvn clean package $*
mvn sonar:sonar $*
