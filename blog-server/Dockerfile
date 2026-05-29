FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY blog-server/pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY blog-server/src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN mkdir -p /app/uploads
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
