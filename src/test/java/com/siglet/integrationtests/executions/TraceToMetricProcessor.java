package com.siglet.integrationtests.executions;

import com.siglet.camel.component.SigletComponent;
import com.siglet.cli.Siglet;
import com.siglet.config.item.ConfigItem;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.ConfigNode;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.concurrent.CountDownLatch;

import static com.siglet.config.ConfigCheckFactory.globalConfigChecker;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class TraceToMetricProcessor {

    public static void main(String[] args) throws Exception {


        var config = """
                receivers:
                - grpc: trace-receiver
                  address: localhost:8080
                  signal-type: trace
                - grpc: metric-receiver
                  address: localhost:8080
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
                        to "metric-receiver" send newGauge {
                          name "derivated metric"
                          unit "tests per second"
                          dataPoint {
                            value 1000
                          }
                        }
                - metric: metric-pipeline
                  from: metric-receiver
                  start: metriclet
                  pipeline:
                  - metriclet: metriclet
                    to: exporter
                    type: processor
                    config:
                      action: |
                        println "metric name=" + thisSignal.getName()
                """;


        Siglet siglet = new Siglet(config);

        siglet.start();

    }
}
