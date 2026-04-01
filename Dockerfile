FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy full project
COPY . .

# Build project inside container
RUN ./mvnw clean package -DskipTests

# Run the generated jar
CMD ["java", "-jar", "target/management-system-0.0.1-SNAPSHOT.jar"]