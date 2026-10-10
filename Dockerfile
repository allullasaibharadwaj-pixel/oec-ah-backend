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
# Build the application with retries for Maven Central rate limits
RUN for i in 1 2 3 4 5; do 
    mvn -B -ntp -Dmaven.wagon.http.retryHandler.count=5 clean package -DskipTests && break; 
    if [ "$i" -eq 5 ]; then exit 1; fi; 
    sleep $((i * 20)); 
done

# Stage 2: Run the application
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]