FROM eclipse-temurin:21-jre
LABEL maintainer="protege.stanford.edu"

EXPOSE 7772
ARG JAR_FILE
COPY target/${JAR_FILE} webprotege-robot-service.jar
ENTRYPOINT ["java","--add-opens=java.management/sun.net=ALL-UNNAMED","-jar","/webprotege-robot-service.jar"]
