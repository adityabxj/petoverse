# ---------- BUILD STAGE ----------
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven wrapper + project config first (cache benefits)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Pre-download dependencies (cached between builds)
RUN ./mvnw dependency:go-offline -DskipTests

# Copy source code after caching
COPY src src

# Build the application
RUN ./mvnw -DskipTests package

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy only the built JAR
COPY --from=build /app/target/*.jar app.jar

# OPTIONAL: only for documentation
EXPOSE 8086

CMD ["java", "-jar", "app.jar"]
