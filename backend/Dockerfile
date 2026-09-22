# --- Etape 1 : construction ---------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Les dependances sont resolues avant la copie des sources : le cache Docker
# n'est invalide que lorsque le pom change.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

# --- Etape 2 : execution ------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# L'application tourne sans privilege.
RUN addgroup -S sportstore && adduser -S sportstore -G sportstore \
    && chown -R sportstore:sportstore /app

COPY --from=build --chown=sportstore:sportstore /build/target/*.jar /app/app.jar

USER sportstore
EXPOSE 3500
ENV JAVA_OPTS="-XX:MaxRAMPercentage=70"

HEALTHCHECK --interval=15s --timeout=5s --start-period=60s --retries=5 \
    CMD wget -qO- http://127.0.0.1:3500/api/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
