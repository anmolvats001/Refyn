# Use lightweight Java runtime
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy jar file (make sure you build it first)
COPY build/libs/*.jar app.jar

# Expose port (your service runs on 8086)
EXPOSE 8086

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]