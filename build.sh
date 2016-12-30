#!/bin/bash
mvn clean package

cp ./binary/target/hotX.tar.gz ./hotX.tar.gz
