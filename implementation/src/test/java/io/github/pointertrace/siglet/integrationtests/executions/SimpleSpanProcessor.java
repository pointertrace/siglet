package io.github.pointertrace.siglet.integrationtests.executions;

import io.github.pointertrace.siglet.impl.Siglet;

import java.util.concurrent.CountDownLatch;

public class SimpleSpanProcessor {

    public static void main(String[] args) throws Exception {


        var config = """
                global:
                  internal-metrics-endpoint-url: http://localhost:4318/v1/metrics
                  internal-metrics-export-interval-millis: 1000
                receivers:
                  - grpc: receiver
                    config:
                      address: localhost:8091
                exporters:
                  - grpc: exporter
                    config:
                      address: localhost:4317
                pipelines:
                  - name: trace-pipeline
                    from: receiver
                    start: print-spanId
                    processors:
                      - spanlet-groovy-action: print-spanId
                        to: exporter
                        thread-pool-size: 1
                        config:
                          action: |
                            println "spanId=" + signal.spanIdEx
                            signal.name = "prefix-" + signal.name
                """;

        Siglet siglet = new Siglet(config);

        CountDownLatch latch = new CountDownLatch(1);

        siglet.start();

        Runtime.getRuntime().addShutdownHook(new Thread(latch::countDown));

        latch.await();

        siglet.stop();

    }
}
