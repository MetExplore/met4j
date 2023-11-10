FROM eclipse-temurin:17-jdk-alpine

RUN apk update && apk add maven

RUN mkdir -p /opt/bin

COPY ./docker_files/met4j.sh /opt/bin
COPY ./met4j-toolbox/target/met4j*.jar /opt/bin/met4j.jar

RUN chmod a+x /opt/bin/met4j.sh

RUN cd /usr/bin && ln -s /opt/bin/met4j.sh

