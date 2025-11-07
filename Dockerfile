# Etapa 1: Build
FROM gradle:8.5-jdk11 AS build

WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle

# Descargar dependencias (se cachea si no cambian)
RUN gradle dependencies --no-daemon || return 0

# Copiar el código fuente
COPY src ./src

# Compilar la aplicación (sin tests para build más rápido)
RUN gradle build --no-daemon -x test

# Etapa 2: Runtime
FROM openjdk:11-jre-slim

WORKDIR /app

# Copiar el JAR construido desde la etapa de build
COPY --from=build /app/build/libs/*.jar app.jar

# Exponer el puerto (Render usa la variable PORT)
EXPOSE 8080

# Variables de entorno por defecto
ENV PORT=8080 \
    JAVA_OPTS="-Xmx512m -Xms256m"

# Healthcheck opcional
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/ || exit 1

# Comando para ejecutar la aplicación
# Usa $PORT de Render automáticamente
CMD java $JAVA_OPTS -jar app.jar