# Stage 1: Build stage
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy pom.xml first to cache dependencies
COPY pom.xml .

# Go offline to cache dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Production runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Add non-root user for security
RUN groupadd -r appgroup && useradd -r -g appgroup appuser
USER appuser

# Expose the application port
EXPOSE 8080

# Run the application (use PORT environment variable if available)
ENTRYPOINT ["java", "-Xmx256m", "-Xss512k", "-jar", "app.jar"]
