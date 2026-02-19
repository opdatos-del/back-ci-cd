# Etapa 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copiar archivos Maven
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn

# Copiar código fuente y SDK local
COPY src src
COPY Link-OS_SDK Link-OS_SDK

# Dar permisos de ejecución a mvnw
RUN chmod +x mvnw

# Instalar la dependencia local de Zebra SDK
RUN ./mvnw install:install-file \
    -Dfile=Link-OS_SDK/PC/v2.15.5553/lib/ZSDK_API.jar \
    -DgroupId=com.zebra.sdk \
    -DartifactId=zsdk_api \
    -Dversion=2.15.5553 \
    -Dpackaging=jar

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
