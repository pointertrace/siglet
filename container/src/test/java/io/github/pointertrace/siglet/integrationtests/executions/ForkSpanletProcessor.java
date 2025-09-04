package io.github.pointertrace.siglet.integrationtests.executions;

import io.github.pointertrace.siglet.container.Siglet;
import io.github.pointertrace.siglet.parser.YamlParser;

public class ForkSpanletProcessor {

    public static void main(String[] args) throws Exception {

    YamlParser configParser= new YamlParser();


        var configFile = """
                receivers:
                - grpc: receiver
                  address: localhost:8080
                exporters:
                - grpc: first-exporter
                  address: localhost:4317
                - grpc: second-exporter
                  address: localhost:4444
                pipelines:
                - trace: pipeline
                  from: receiver
                  start:
                  - trace-aggregator
                  pipeline:
                  - trace-aggregator: trace-aggregator
                    to: router
                    type: default
                    config:
                      inactive-timeout-millis: 2000
                  - tracelet: router
                    to:
                    - first-exporter
                    - second-exporter
                    type: router
                    config:
                      default: first-exporter
                      routeConfigs:
                      - when: trace[0].spanId % 2 == 0
                        to: second-exporter
                """;


        Siglet siglet = new Siglet(configFile);

        siglet.start();


    }
}
