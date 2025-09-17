# Base image with Tomcat and JDK 21
FROM tomcat:10.1-jdk21

# Set working directory
WORKDIR /usr/local/tomcat/webapps

# Copy the WAR file into Tomcat
COPY target/*.war ROOT.war

# Expose default Tomcat port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
