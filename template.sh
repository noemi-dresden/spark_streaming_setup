#!/bin/bash

cd /usr/src/app
echo ${SPARK_APPLICATION_JAR_LOCATION}
cp target/${SPARK_APPLICATION_JAR_NAME} ${SPARK_APPLICATION_JAR_LOCATION}

sh /submit.sh