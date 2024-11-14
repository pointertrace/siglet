package com.siglet.integrationtests.executions;

import com.siglet.cli.Siglet;

public class ViajarProcessor {

    public static void main(String[] args) throws Exception {

//        to "metric-receiver" send newGauge {
//            name "derivated metric"
//            unit "tests per second"
//            dataPoint {
//                value 1000
//            }
//        }

        var config = """
                receivers:
                - grpc: trace-receiver
                  address: localhost:8081
                  signal-type: trace
                - grpc: metric-receiver
                  address: localhost:8081
                  signal-type: metric
                exporters:
                - grpc: exporter
                  address: localhost:4317
                pipelines:
                - trace: trace-pipeline
                  from: trace-receiver
                  start: spanlet
                  pipeline:
                  - spanlet: spanlet
                    to: exporter
                    type: processor
                    config:
                      action: |
                        println "span spanId=" + thisSignal.getSpanId()
                """;


        Siglet siglet = new Siglet(config);

        siglet.start();

    }
}
