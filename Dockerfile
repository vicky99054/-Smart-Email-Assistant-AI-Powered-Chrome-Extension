# ─────────────────────────────────────────
# Stage 1: Build the JAR
# ─────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy pom.xml first (better layer caching)
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src

RUN mvn clean package -DskipTests -B

# ─────────────────────────────────────────
# Stage 2: Run the JAR (slim image)
# ─────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]