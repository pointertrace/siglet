package io.github.pointertrace.siglet.integrationtests.executions;

import io.github.pointertrace.siglet.impl.Siglet;

import java.util.concurrent.CountDownLatch;

public class SimpleSpanProcessor {

    public static void main(String[] args) throws Exception {


        var config = """
                global:
                  internal-metrics-grpc-exporter: exporter-metrics
                  internal-metrics-export-interval-millis: 1000
                  queue-size: 2000
                receivers:
                  - grpc: receiver
                    config:
                      address: localhost:8081
                pipelines:
                  - name: trace-pipeline
                    from: receiver
                    start: print-spanId
                    processors:
                      - spanlet-groovy-action: print-spanId
                        config:
                          action: |
                            signal.name = "prefix-" + signal.name
                        to: exporter-traces
                exporters:
                  - grpc: exporter-metrics
                    config:
                      address: localhost:4317
                  - grpc: exporter-traces
                    config:
                      address: localhost:8082
                """;

        Siglet siglet = new Siglet(config);

        CountDownLatch latch = new CountDownLatch(1);

        siglet.start();

        Runtime.getRuntime().addShutdownHook(new Thread(latch::countDown));

        latch.await();

        siglet.stop();

    }

}
