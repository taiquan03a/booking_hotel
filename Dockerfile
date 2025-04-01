# Sử dụng Maven để build ứng dụng
FROM maven:3.9.0-eclipse-temurin-17 AS build

# Đặt thư mục làm việc trong container
WORKDIR /app

# Copy file cấu hình Maven và source code vào container
COPY pom.xml .
COPY src ./src

# Biên dịch ứng dụng và đóng gói thành file JAR
RUN mvn clean package -DskipTests

# Sử dụng JDK 17 nhẹ hơn để chạy ứng dụng
FROM eclipse-temurin:17-jdk-alpine

# Đặt thư mục làm việc trong container
WORKDIR /app

# Copy file JAR từ quá trình build vào container
COPY --from=build /app/target/*.jar app.jar


# Expose cổng mà ứng dụng chạy
EXPOSE 8080

# Lệnh để chạy ứng dụng khi container khởi động
ENTRYPOINT ["java", "-jar", "app.jar"]
