#!/usr/bin/env bash
java -jar a.jar 1>info 2>log &
java -jar b.jar
