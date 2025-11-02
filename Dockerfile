# Use OpenJDK 17
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY target/*.jar app.jar

EXPOSE 8080
  # Still okay to expose 8080 for local use

ENTRYPOINT ["java", "-jar", "app.jar"]
