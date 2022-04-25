FROM debian:stable-slim

RUN export DEBIAN_FRONTEND=noninteractive

RUN apt-get update \
&& apt-get upgrade -y \
&& apt-get install -y openjdk-11-jre git maven \
&& apt-get clean \
&& apt-get purge

RUN mkdir -p /opt && cd /opt \
&& git clone https://forgemia.inra.fr/metexplore/met4j.git \
&& cd met4j \
&& mvn install \
&& cd met4j-toolbox \
&& mvn package

RUN mkdir -p /opt/bin \
&& cd /opt/met4j/met4j-toolbox \
&& cp target/met4j*.jar /opt/bin/met4j.jar \
&& cd /opt \
&& rm -rf met4j ~/.m2  \
&& apt-get remove -y git && apt-get autoremove -y

COPY ./docker_files/met4j.sh /opt/bin

ENTRYPOINT ["/opt/bin/met4j.sh"]

