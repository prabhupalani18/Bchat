# Build stage
FROM maven:3.9.0-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src src
RUN mvn -B -DskipTests package

# Run stage - minimal JRE
FROM eclipse-temurin:17-jre
ARG JAR_FILE=/workspace/target/bchat-webhook-service-1.0.0.jar
COPY --from=build ${JAR_FILE} /app/bchat-webhook-service.jar
EXPOSE 8080
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-jar","/app/bchat-webhook-service.jar"]
