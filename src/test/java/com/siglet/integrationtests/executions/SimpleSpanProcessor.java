package com.siglet.integrationtests.executions;

import com.siglet.cli.Siglet;

public class SimpleSpanProcessor {

    public static void main(String[] args) throws Exception {


        var config = """
                receivers:
                - grpc: trace-receiver
                  address: localhost:8081
                  signal: trace
                exporters:
                - grpc: exporter
                  address: localhost:50051
                  batch-size-in-signals: 4
                pipelines:
                - trace: trace-pipeline
                  from: trace-receiver
                  start: imprime span
                  pipeline:
                  - spanlet: imprime span
                    to: exporter
                    type: processor
                    config:
                      action: |
                        println "spanId=" + thisSignal.spanIdEx
                """;

        Siglet siglet = new Siglet(config);

        siglet.start();

    }
}
