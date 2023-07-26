FROM adoptopenjdk/openjdk11:alpine-jre
ARG JAR_FILE=target/restaurant-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} restaurant.jar
ENTRYPOINT ["java","-jar","/restaurant.jar"]
