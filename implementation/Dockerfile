FROM openjdk:24-slim
WORKDIR /opt
COPY target/siglet-*.jar /opt/siglet.jar
RUN addgroup --system siglet && adduser --system siglet --ingroup siglet
RUN chown -R siglet:siglet /opt
USER siglet:siglet
ENTRYPOINT ["java","--sun-misc-unsafe-memory-access=allow", "--enable-native-access=ALL-UNNAMED","-jar","/opt/siglet.jar"]