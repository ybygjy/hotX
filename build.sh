#!/bin/bash
mvn clean package

cp ./agent/target/hotX-agent-jar-with-dependencies.jar ./hotX-agent.jar
cp ./core/target/hotX-core-jar-with-dependencies.jar ./hotX-core.jar
