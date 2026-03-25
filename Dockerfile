# we use multi stage dockerfile because the bigger maven image to build the appliccation and smaller image to run it
# stage:1 -> building the application, using an image that has maven and java
FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /app

# copying pom.xml and src code to the container
COPY pom.xml .
COPY src ./src

# building jar file
RUN mvn clean package -DskipTests

# stage:2 -> running the application, we switch to much smaller image that has only JRE no java
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# we only copy finished jar from stage:1
COPY --from=build /app/target/*.jar app.jar

# exposing the port app runs on
EXPOSE 8080

# starting the application
ENTRYPOINT ["java", "-jar", "app.jar"]