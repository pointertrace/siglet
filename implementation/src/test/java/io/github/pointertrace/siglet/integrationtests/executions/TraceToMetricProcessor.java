package io.github.pointertrace.siglet.integrationtests.executions;

import io.github.pointertrace.siglet.impl.Siglet;

public class TraceToMetricProcessor {

    public static void main(String[] args) throws Exception {


        var config = """
                receivers:
                - grpc: trace-receiverDescriptor
                  address: localhost:8080
                  otelSignalType: trace
                - grpc: metric-receiverDescriptor
                  address: localhost:8080
                  otelSignalType: metric
                exporters:
                - grpc: exporterDescriptor
                  address: localhost:4317
                pipelineDescriptors:
                - trace: trace-pipelineDescriptor
                  from: trace-receiverDescriptor
                  start: spanlet
                  pipelineDescriptor:
                  - spanlet: spanlet
                    to: exporterDescriptor
                    type: baseProcessor
                    config:
                      action: |
                        println "span spanId=" + thisSignal.getSpanId()
                        to "metric-receiverDescriptor" send newGauge {
                          name "derivated metric"
                          unit "tests per second"
                          dataPoint {
                            value 1000
                          }
                        }
                - metric: metric-pipelineDescriptor
                  from: metric-receiverDescriptor
                  start: metriclet
                  pipelineDescriptor:
                  - metriclet: metriclet
                    to: exporterDescriptor
                    type: baseProcessor
                    config:
                      action: |
                        println "metric name=" + thisSignal.getName()
                """;


        Siglet siglet = new Siglet(config);

        siglet.start();

    }
}
