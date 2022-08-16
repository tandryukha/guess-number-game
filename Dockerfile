FROM gradle:jdk11 as build
WORKDIR /
COPY . application
WORKDIR /application
RUN gradle build

###Image for run
FROM openjdk:11-jdk-slim as run-image
ARG JAR_FILE=/application/build/libs/guess-num-backend-0.0.1-SNAPSHOT.jar
COPY --from=build ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]