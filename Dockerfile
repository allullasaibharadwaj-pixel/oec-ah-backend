# Stage 1: Build the Spring Boot application
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven configuration first
COPY pom.xml .

# Download dependencies separately so Docker can cache them
RUN mvn -B -ntp dependency:go-offline

# Copy application source
COPY src ./src

# Build the application
# Build the application
RUN mvn -B -ntp -Daether.transport.http.retryHandler.count=5 -Daether.transport.http.retryHandler.interval=10000 -Daether.transport.http.retryHandler.intervalMax=60000 clean package -DskipTests
# Stage 2: Run the application
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]