package io.github.pointertrace.siglet.integrationtests.executions;

import io.github.pointertrace.siglet.impl.Siglet;

import java.util.concurrent.CountDownLatch;

public class SimpleSpanProcessor_noOut {

    public static void main(String[] args) throws Exception {

//        internal-metrics-endpoint-url: http://localhost:4318/v1/metrics

        var config = """
                global:
                  # Envia metricas internas para o Grafana LGTM (ativo apenas com profile 'monitor')
                  internal-metrics-grpc-exporter: exporter-metrics
                  internal-metrics-export-interval-millis: 1000
                  queue-size: 2000
                receivers:
                  - grpc: receiver
                    config:
                      address: 0.0.0.0:8081
                      max-inbound-message-size-bytes: 33554432
                      flow-control-window-bytes: 16777216
                      max-concurrent-calls-per-connection: 4096
                      max-inbound-metadata-size-bytes: 16384
                      keep-alive-time-seconds: 10
                      keep-alive-timeout-seconds: 5
                      permit-keep-alive-time-seconds: 5
                exporters:
                  - grpc: exporter
                    config:
                      address: localhost:8082
                  - grpc: exporter-metrics
                    config:
                      address: localhost:4317
                pipelines:
                  - name: benchmark-pipeline
                    from: receiver
                    start: suffix-spanlet
                    processors:
                      - spanlet-groovy-action: suffix-spanlet
                        config:
                          action: signal.name = signal.name + '-suffix'
                        to: exporter
                """;

        Siglet siglet = new Siglet(config);

        CountDownLatch latch = new CountDownLatch(1);

        siglet.start();

        Runtime.getRuntime().addShutdownHook(new Thread(latch::countDown));

        latch.await();

        siglet.stop();

    }

}
