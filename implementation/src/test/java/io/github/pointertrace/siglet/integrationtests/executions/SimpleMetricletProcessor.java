package io.github.pointertrace.siglet.integrationtests.executions;

import io.github.pointertrace.siglet.impl.Siglet;

public class SimpleMetricletProcessor {


    public static void main(String[] args) throws Exception {

        var configFile = """
                receivers:
                - grpc: receiverDescriptor
                  address: localhost:4317
                  otelSignalType: metric
                exporters:
                - grpc: exporterDescriptor
                  address: localhost:4317
                  batchSizeInSignals: 3
                pipelineDescriptors:
                - metric: simple pipelineDescriptor
                  from: receiverDescriptor
                  start: first metriclet
                  pipelineDescriptor:
                  - metriclet: first metriclet
                    to: exporterDescriptor
                    type: baseProcessor
                    config:
                      action: >
                        metric.setName("prefix-" + metric.getName())
                        println "metric-name="+ metric.getName()
                """;

        Siglet siglet = new Siglet(configFile);

        siglet.start();
    }
}
