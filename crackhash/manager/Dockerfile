FROM openjdk:17

WORKDIR /app

# Copy the jar file and the XSD schema file to the container
COPY build/libs/manager.jar /app/worker.jar
#COPY hash-crack.xsd /app/hash-crack.xsd

# Expose the port on which the service will run
EXPOSE 8081

CMD ["java", "-jar", "/app/worker.jar"]
