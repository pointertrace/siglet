FROM  eclipse-temurin:21.0.4_7-jdk-alpine
ARG VERSION
RUN addgroup -S siglet && adduser -S siglet -G siglet
USER siglet:siglet
COPY target/siglet-${VERSION}.jar /opt/siglet.jar
ENTRYPOINT ["./entrypoint.sh"]
CMD ["default"]