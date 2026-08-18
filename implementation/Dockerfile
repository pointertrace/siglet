FROM eclipse-temurin:25

ARG JAR_FILE

RUN addgroup --system siglet \
    && adduser --system \
        --home /home/siglet \
        --ingroup siglet \
        siglet \
    && mkdir -p /home/siglet /opt/app /opt/extra \
    && chown -R siglet:siglet /home/siglet /opt

ENV HOME=/home/siglet

WORKDIR /opt/app

COPY --chown=siglet:siglet ${JAR_FILE} /opt/app/siglet.jar

USER siglet:siglet

ENTRYPOINT ["java","--sun-misc-unsafe-memory-access=allow","--enable-native-access=ALL-UNNAMED","-jar","/opt/app/siglet.jar"]