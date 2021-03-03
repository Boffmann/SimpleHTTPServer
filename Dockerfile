FROM openjdk:11
COPY SimpleHTTPServer/target/SimpleHTTPServer-1.0-SNAPSHOT.jar /server/server.jar
CMD ["java", "-jar", "/server/server.jar"]
