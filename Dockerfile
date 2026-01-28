#   # Base image with JDK/JRE 21
#   FROM eclipse-temurin:21-jre
#
#   # Set working directory inside the container
#   WORKDIR /app
#
#   # Copy the built JAR into the container
#   COPY target/*.jar app.jar
#
#   # Expose the port your Spring Boot app listens on
#   EXPOSE 8080
#
#   # Run the application
#   ENTRYPOINT ["java", "-jar", "app.jar"]
