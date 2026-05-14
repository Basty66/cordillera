FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY ms-datos-org/mvnw ms-datos-org/pom.xml ./
COPY ms-datos-org/.mvn ./.mvn/
COPY ms-datos-org/src ./src
RUN chmod +x mvnw && ./mvnw clean package -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
