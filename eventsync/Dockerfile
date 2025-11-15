FROM maven:3.9.1-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*-SNAPSHOT.jar app.jar

ENV SERVER_PORT=$PORT
EXPOSE $PORT

ENTRYPOINT ["java", "-jar", "app.jar"]




