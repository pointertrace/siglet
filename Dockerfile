FROM openjdk:24-slim
COPY target/siglet-*.jar /opt/siglet.jar
RUN addgroup -S siglet && adduser -S siglet -G siglet
RUN chown -R siglet:siglet /opt
USER siglet:siglet
ENTRYPOINT ["java","-jar","/opt/siglet.jar"]
