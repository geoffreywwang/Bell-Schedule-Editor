#File used to build and bundle application as a DMG
#!/usr/bin/env bash
mvn clean compile assembly:single -DskipTests
mvn package -DskipTests
mvn package appbundle:bundle -DskipTests