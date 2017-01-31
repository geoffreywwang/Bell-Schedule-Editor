#!/usr/bin/env bash
mvn clean compile assembly:single -DskipTests
mvn package -DskipTests
mvn package appbundle:bundle -DskipTests