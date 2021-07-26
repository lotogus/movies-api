#### Stage 1: Build the application
FROM openjdk:11.0.12-jdk-slim as build

COPY . ./
RUN ./mvnw package

#### Stage 2: A minimal docker image with command to run the app
FROM openjdk:11.0.12-jre-slim
# Copy project dependencies from the build stage
COPY --from=build infra/target/infra-0.0.1-SNAPSHOT.jar /app/app.jar

ENTRYPOINT ["java","-jar","app.jar"]