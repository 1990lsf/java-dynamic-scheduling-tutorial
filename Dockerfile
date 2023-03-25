FROM eclipse-temurin:8-jre-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["sh","-c","java ${JAVA_OPTS} -jar /app.jar ${@}"]
