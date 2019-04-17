FROM bde2020/spark-submit:2.4.0-hadoop2.7

MAINTAINER Salohy Miarisoa


ENV SPARK_APPLICATION_MAIN_CLASS de.tudresden.inf.rn.movebis.sparkstreaming.Main
ENV SPARK_APPLICATION_JAR_NAME movebis-spark-streaming-1.0-SNAPSHOT.jar
ENV SPARK_APPLICATION_JAR_LOCATION "app"

COPY template.sh /

RUN apk add --no-cache openjdk8 maven\
      && chmod +x /template.sh \
      && mkdir -p /usr/src/app

# Copy the POM-file first, for separate dependency resolving and downloading
COPY pom.xml /usr/src/app
RUN cd /usr/src/app \
      && mvn dependency:resolve

RUN cd /usr/src/app \
      && mvn verify

# Copy the source code and build the application
COPY . /usr/src/app
RUN cd /usr/src/app \
      && mvn clean package


CMD ["/bin/bash", "/template.sh"]