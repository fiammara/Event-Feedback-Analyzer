# Stage 1: Build the JAR
FROM maven:3.9.1-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

# Build the JAR inside Docker
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy the built JAR
COPY --from=build /app/target/eventsync-0.0.1-SNAPSHOT.jar app.jar

# Use Railway port
ENV SERVER_PORT=${PORT:-8080}
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]




