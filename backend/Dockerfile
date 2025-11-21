# Build Stage (Maven + JDK)
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn

RUN chmod +x mvnw

RUN ./mvnw -q dependency:resolve

COPY src src

RUN ./mvnw -q -DskipTests package


# Runtime Stage (minimal)
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
