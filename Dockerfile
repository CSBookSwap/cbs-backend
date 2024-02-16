FROM eclipse-temurin:21-jdk-alpine as build-jdk

COPY jdk-module.info .

RUN jlink --add-modules $(cat jdk-module.info) --strip-java-debug-attributes  \
    --no-man-pages --no-header-files --output jdk

FROM alpine:3.19

VOLUME /tmp

ARG APPLICATION_USER=spring-app

ENV JAVA_HOME=/jdk
ENV PATH="${PATH}:${JAVA_HOME}/bin"

RUN adduser --no-create-home -u 1000 -D $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER 1000

WORKDIR /app

COPY --from=build-jdk jdk $JAVA_HOME
COPY extracted-jar/spring-boot-loader/ ./
COPY extracted-jar/dependencies/ ./
COPY extracted-jar/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "-cp", "/app", "org.springframework.boot.loader.JarLauncher"]