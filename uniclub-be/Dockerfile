# Stage 1: Build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application (skip tests for faster build)
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Tạo group và user non-root để tuân thủ nguyên tắc Least Privilege
RUN addgroup -S spring && adduser -S spring -G spring

# Copy jar from build stage và phân quyền cho user spring
COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

# Chuyển quyền thực thi sang user spring
USER spring:spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application với cờ tối ưu RAM trong môi trường container
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
