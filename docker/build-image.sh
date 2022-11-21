#!/bin/bash

# Ask maven to build the executable jar file from the source files
mvn clean install --file ../../MockMock/pom.xml

# Copy the executable jar file in the current directory
cp ../../MockMock/MockMock.jar .

# Build the Docker image locally
docker build --tag ycochet/mockmock .