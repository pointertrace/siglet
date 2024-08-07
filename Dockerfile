FROM  eclipse-temurin:21.0.4_7-jdk-alpine
ARG PROJECT_VERSION
RUN addgroup -S siglet && adduser -S siglet -G siglet
RUN #mkdir /opt/app
USER siglet:siglet
COPY target/siglet-${PROJECT_VERSION}.jar /opt/siglet.jar
ENTRYPOINT ["java","-jar","/opt/siglet.jar"]