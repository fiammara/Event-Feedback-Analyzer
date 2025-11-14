# Stage 1: Build the JAR
FROM maven:3.9.1-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build the JAR (skip tests)
RUN mvn clean package -DskipTests

# Stage 2: Runtime (use JRE, not JDK)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the JAR (verify it exists)
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar

# Railway sets $PORT
ENV SERVER_PORT=$PORT
EXPOSE $PORT

# Run with explicit classpath (safer)
ENTRYPOINT ["java", "-jar", "app.jar"]




