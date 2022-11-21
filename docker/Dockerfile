FROM eclipse-temurin:11

# Install netcat, so that we can demonstrate container-to-container communication
  
#
# When we build the image, we copy the executable jar in the image file system. 
#
COPY MockMock.jar /opt/app/MockMock.jar

#
# Our application will accept TCP/HTTP connections on port 8080 and TCP/SMTP connexion on port 25000
#
EXPOSE 25000
EXPOSE 8080

#
# This is the command that is executed when the Docker container starts
#
CMD ["java", "-jar", "/opt/app/MockMock.jar", "-p", "25000", "-h", "8080"]