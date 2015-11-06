#!/bin/bash
mvn clean package

cp ./target/hotX-agent-jar-with-dependencies.jar ./hotX-agent.jar
