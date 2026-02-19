# Etapa 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copiar archivos Maven
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn

# Copiar código fuente
COPY src src

# Dar permisos de ejecución a mvnw
RUN chmod +x mvnw

# Compilar el proyecto
RUN ./mvnw clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar el JAR desde la etapa de build
COPY --from=builder /build/target/*.jar app.jar

# Exponer puerto (cambia según tu configuración)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
