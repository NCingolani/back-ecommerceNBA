# Usamos una imagen oficial de Java 17
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copiamos el archivo compilado (JAR) desde la carpeta target
COPY target/ecommerce-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]