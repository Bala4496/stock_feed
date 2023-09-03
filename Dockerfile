# Building the Spring Boot JAR
FROM 554040323477.dkr.ecr.us-east-1.amazonaws.com/stock-feed-openjdk-17:maven AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -Dmaven.test.skip

# Creating the Docker image
FROM 554040323477.dkr.ecr.us-east-1.amazonaws.com/stock-feed-openjdk-17:openjdk
WORKDIR /app
EXPOSE 8080
COPY --from=build /app/target/stocks_feed-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]