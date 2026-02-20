# Etapa 1: Build
FROM eclipse-temurin:21-jdk-windowsservercore-ltsc2022 AS builder

WORKDIR /build

# Copiar archivos Maven
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn

# Copiar código fuente y SDK local
COPY src src
COPY Link-OS_SDK Link-OS_SDK

# Compilar el proyecto (usa mvnw.cmd en Windows)
RUN mvnw.cmd clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre-windowsservercore-ltsc2022

WORKDIR /app

# Copiar el JAR desde la etapa de build
COPY --from=builder /build/target/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
