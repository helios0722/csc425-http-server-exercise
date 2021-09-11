#!/bin/bash
mvn test
mvn exec:java &
sleep 30
/node_modules/.bin/newman run postman.json
