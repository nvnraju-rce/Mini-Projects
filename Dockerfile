# Build the jar first:  mvn clean package
FROM eclipse-temurin:17-jre
COPY target/mini-shop-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
