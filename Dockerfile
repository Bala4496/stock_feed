# Building the Spring Boot JAR
FROM 554040323477.dkr.ecr.eu-central-1.amazonaws.com/openjdk-17:maven AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -Dmaven.test.skip

# Creating the Docker image
FROM 554040323477.dkr.ecr.eu-central-1.amazonaws.com/openjdk-17:openjdk
WORKDIR /app
COPY --from=build /app/target/stocks_feed-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]