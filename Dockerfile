#
# Build stage
#
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /home/app
COPY pipeline/pom.xml .
COPY pipeline/src ./src
RUN mvn clean package -DskipTests -Pmonitoring

#
# Runtime stage
#
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /home/app/target/pipeline*.jar app.jar
COPY --from=build /home/app/target/agents/*.jar otel-agent.jar
EXPOSE 8080
ENTRYPOINT ["java", "-javaagent:otel-agent.jar", "-jar", "app.jar"]