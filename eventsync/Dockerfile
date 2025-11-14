# ========================
# Stage 1: Build the JAR
# ========================
FROM maven:3.9.1-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven config and source
COPY pom.xml .
COPY src ./src

# Build the Spring Boot JAR
RUN mvn clean package -DskipTests

# ========================
# Stage 2: Runtime
# ========================
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/eventsync-0.0.1-SNAPSHOT.jar app.jar

# Expose the container port
EXPOSE 8080

# Set Railway PORT environment variable
ENV SERVER_PORT=${PORT:-8080}

# Run the Spring Boot application
ENTRYPOINT ["java","-jar","app.jar"]




