# ========== 1️⃣ Stage build ==========
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy toàn bộ source code
COPY . .

# Build ứng dụng (bỏ qua test để giảm thời gian build)
RUN mvn clean package -DskipTests

# ========== 2️⃣ Stage run ==========
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Copy file jar đã build từ stage 1
COPY --from=build /app/target/*.jar app.jar

# Expose port cho container
EXPOSE 8297

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
