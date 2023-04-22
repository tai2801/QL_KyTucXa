#!/bin/sh
set -e
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java") tests/TestDorm.java
java -cp out dorm.TestDorm
