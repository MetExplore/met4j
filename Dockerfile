FROM eclipse-temurin:17-jre-alpine

RUN apk add --no-cache bash

RUN mkdir /opt/bin

COPY ./docker_files/met4j.sh /opt/bin/met4j.sh
COPY ./met4j-toolbox/target/met4j*.jar /opt/bin/met4j.jar

RUN chmod 755 /opt/bin/met4j.sh

RUN ln -s /opt/bin/met4j.sh /usr/bin/met4j.sh

#ENTRYPOINT ["/opt/bin/met4j.sh"]
ENTRYPOINT ["bash", "/opt/bin/met4j.sh"]

