FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn -q -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -B -DskipTests package

FROM eclipse-temurin:21-jre
ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE=default
WORKDIR /app

COPY --from=builder /app/target/*.jar /app/app.jar

RUN useradd -r -u 1001 spring && chown -R spring:spring /app
USER spring

EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
