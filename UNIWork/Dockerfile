# ========== 1️⃣ Stage build ==========
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy chỉ subfolder chứa Spring Boot
COPY . .


# Build ứng dụng (bỏ qua test để giảm thời gian build)
RUN mvn clean package -DskipTests

# ========== 2️⃣ Stage run ==========
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Copy file jar đã build từ stage 1
COPY --from=build /app/target/*.jar app.jar

# Expose port để Render map (không bắt buộc nhưng tốt)
EXPOSE 8080

# Chạy ứng dụng và dùng biến PORT
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
