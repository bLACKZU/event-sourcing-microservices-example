FROM maven:3.9.6-eclipse-temurin-21 as builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM bellsoft/liberica-runtime-container:jre-21-musl 
WORKDIR /app
ARG JAR_FILE
COPY --from=builder /app/target/${JAR_FILE} app.jar
RUN groupadd -r -g 2000 java && useradd -m -d /home/java/ -u 2000 -r -g java java
RUN chown -R java:java /app
USER java
EXPOSE 8761
CMD ["java", "-jar", "app.jar"] 