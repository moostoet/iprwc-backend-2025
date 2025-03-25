FROM openjdk:17-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar
RUN chown spring:spring app.jar
USER spring
ENTRYPOINT ["java", "-jar", "app.jar"]