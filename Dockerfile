# Stage 1: 构建前端
FROM node:18-alpine AS frontend-builder
WORKDIR /app
COPY blog-web/package.json blog-web/package-lock.json ./
RUN npm ci
COPY blog-web/ ./
RUN npx vite build

# Stage 2: 构建后端（前端产物复制到 static）
FROM maven:3.9-eclipse-temurin-17-alpine AS backend-builder
WORKDIR /app
COPY blog-server/pom.xml ./
RUN mvn dependency:go-offline -B
COPY blog-server/src ./src
COPY --from=frontend-builder /app/dist/ ./src/main/resources/static/
RUN mvn package -DskipTests -B

# Stage 3: 运行时
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=backend-builder /app/target/*.jar app.jar
RUN mkdir -p /app/uploads
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
