package com.siglet.integrationtests.executions;

import com.siglet.container.Siglet;

import java.util.concurrent.CountDownLatch;

public class SimpleSpanProcessor {

    public static void main(String[] args) throws Exception {


        var config = """
                receivers:
                  - grpc: receiver
                    address: localhost:8080
                    signal: trace
                exporters:
                  - grpc: exporter
                    address: localhost:4317
                    batch-size-in-signals: 1
                pipelines:
                  - name: trace-pipeline
                    signal: trace
                    from: receiver
                    start: print spanId
                    processors:
                      - name: print spanId
                        kind: spanlet
                        to: exporter
                        type: groovy-action
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
