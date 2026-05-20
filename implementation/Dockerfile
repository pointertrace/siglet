FROM eclipse-temurin:25
ARG JAR_FILE
RUN mkdir /opt/app
WORKDIR /opt/app
COPY $JAR_FILE /opt/app/siglet.jar
RUN addgroup --system siglet && adduser --system siglet --ingroup siglet
RUN chown -R siglet:siglet /opt/app
USER siglet:siglet
ENTRYPOINT ["java","--sun-misc-unsafe-memory-access=allow", "--enable-native-access=ALL-UNNAMED","-jar","/opt/app/siglet.jar"]