FROM adoptopenjdk/openjdk11:alpine-jre
ARG JAR_FILE=target/delivery-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} delivery.jar
ENTRYPOINT ["java","-jar","/delivery.jar"]
