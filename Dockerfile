#
# Build stage
#
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /home/app
COPY pipeline/pom.xml .
COPY pipeline/src ./src
RUN mvn clean package -DskipTests

#
# Runtime stage
#
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /home/app/target/*.jar app.jar
COPY wait-for-mysql.sh .
RUN apt-get update && apt-get install -y netcat \
    && chmod +x wait-for-mysql.sh
EXPOSE 8080
ENTRYPOINT ["sh","./wait-for-mysql.sh"]
