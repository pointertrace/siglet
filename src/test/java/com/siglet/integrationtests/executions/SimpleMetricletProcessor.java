package com.siglet.integrationtests.executions;

import com.siglet.container.Siglet;
import com.siglet.parser.YamlParser;

public class SimpleMetricletProcessor {


    public static void main(String[] args) throws Exception {

        YamlParser configParser = new YamlParser();

        var configFile = """
                receivers:
                - grpc: receiver
                  address: localhost:4317
                  otelSignalType: metric
                exporters:
                - grpc: exporter
                  address: localhost:4317
                  batchSizeInSignals: 3
                pipelines:
                - metric: simple pipeline
                  from: receiver
                  start: first metriclet
                  pipeline:
                  - metriclet: first metriclet
                    to: exporter
                    type: baseEventloopProcessor
                    config:
                      action: >
                        metric.setName("prefix-" + metric.getName())
                        println "metric-name="+ metric.getName()
                """;

        Siglet siglet = new Siglet(configFile);

        siglet.start();
    }
}
