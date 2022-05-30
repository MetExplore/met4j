FROM debian:stable-slim

RUN export DEBIAN_FRONTEND=noninteractive

RUN apt-get update \
&& apt-get upgrade -y \
&& apt-get install -y openjdk-11-jre maven \
&& apt-get clean \
&& apt-get purge

RUN mkdir -p /opt/bin

COPY ./docker_files/met4j.sh /opt/bin
COPY ./met4j-toolbox/target/met4j*.jar /opt/bin/met4j.jar

RUN chmod a+x /opt/bin/met4j.sh

RUN cd /usr/bin && ln -s /opt/bin/met4j.sh

