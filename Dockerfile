# Building the Spring Boot JAR
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Creating the Docker image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/stocks_feed-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]