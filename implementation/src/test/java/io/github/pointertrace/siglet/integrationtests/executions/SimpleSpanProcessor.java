package io.github.pointertrace.siglet.integrationtests.executions;

import io.github.pointertrace.siglet.impl.Siglet;

import java.util.concurrent.CountDownLatch;

public class SimpleSpanProcessor {

    public static void main(String[] args) throws Exception {


        var config = """
                receivers:
                  - grpc: receiver
                    address: localhost:8080
                exporters:
                  - grpc: exporter
                    address: localhost:4317
                pipelines:
                  - name: trace-pipeline
                    from: receiver
                    start: print spanId
                    processors:
                      - name: print spanId
                        kind: spanlet
                        to: exporter
                        type: groovy-action
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
