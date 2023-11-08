# Builder stage
FROM openjdk:17-jdk-alpine as builder
WORKDIR book-store
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} book-store.jar
RUN java -Djarmode=layertools -jar book-store.jar extract

# Final stage
FROM openjdk:17-jdk-alpine
WORKDIR application
COPY --from=builder book-store/dependencies/ ./
COPY --from=builder book-store/spring-boot-loader/ ./
COPY --from=builder book-store/snapshot-dependencies/ ./
COPY --from=builder book-store/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
EXPOSE 8080
