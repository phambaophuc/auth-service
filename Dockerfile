FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package

FROM openjdk:17
EXPOSE 8998
COPY --from=build /target/auth-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]