# Build
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q -B dependency:go-offline
COPY src ./src
RUN ./mvnw -q -B -DskipTests package

# Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN apt-get update \
  && apt-get install -y --no-install-recommends curl \
  && rm -rf /var/lib/apt/lists/* \
  && useradd -r -u 10001 algoprep
COPY --from=build /app/target/algoprep-1.0.0-SNAPSHOT.jar /app/app.jar
RUN mkdir -p /app/data/sync && chown -R algoprep:algoprep /app
USER algoprep
ENV JAVA_OPTS="-Xms128m -Xmx512m"
EXPOSE 18080
HEALTHCHECK --interval=15s --timeout=5s --retries=10 CMD \
  curl -fsS http://127.0.0.1:18080/actuator/health || exit 1
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
