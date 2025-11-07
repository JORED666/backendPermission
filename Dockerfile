FROM gradle:8.5-jdk11 AS build

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle

RUN gradle dependencies --no-daemon || return 0

COPY src ./src

RUN gradle build --no-daemon -x test

FROM eclipse-temurin:11-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xmx512m -Xms256m"

CMD java $JAVA_OPTS -Dktor.deployment.port=${PORT:-8080} -jar app.jar