FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY discovery-service/pom.xml ./discovery-service/pom.xml
COPY edge-service/pom.xml ./edge-service/pom.xml
COPY friend-service/pom.xml ./friend-service/pom.xml
COPY user-service/pom.xml ./user-service/pom.xml
COPY recommendation-service/pom.xml ./recommendation-service/pom.xml
RUN mvn dependency:go-offline
COPY user-service/src ./user-service/src
RUN mvn clean package -DskipTests -pl user-service -Ddockerfile.skip=true

FROM eclipse-temurin:21-jre 
WORKDIR /app
ARG JAR_FILE
COPY --from=builder /app/user-service/target/${JAR_FILE} app.jar
RUN groupadd -r -g 2000 java && useradd -m -d /home/java/ -u 2000 -r -g java java
RUN chown -R java:java /app
USER java
EXPOSE 8120
CMD ["java", "-jar", "app.jar"]