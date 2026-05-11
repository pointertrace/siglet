package io.github.pointertrace.siglet.integrationtests.executions;

import io.github.pointertrace.siglet.impl.Siglet;

public class ComplexProcessor {

    public static void main(String[] args) {

        var config = """
                receivers:
                - grpc: trace-receiverDescriptor
                  address: localhost:8081
                  otelSignalType: trace
                - grpc: metric-receiverDescriptor
                  address: localhost:8081
                  otelSignalType: metric
                exporters:
                - grpc: exporterDescriptor
                  address: localhost:4317
                pipelineDescriptors:
                - trace: trace-pipelineDescriptor
                  from: trace-receiverDescriptor
                  start: alteracao nome span wicket
                  pipelineDescriptor:
                  - spanlet: alteracao nome span wicket
                    to:
                    - trace aggregator
                    - exporterDescriptor
                    type: baseProcessor
                    config:
                      action: |
                        when { thisSignal.attributes.containsKey("br.gov.bcb.trace.wicket.page") } then {
                          span {
                            name "wicket:" + thisSignal.attributes["br.gov.bcb.trace.wicket.page"]
                          }
                        }
                  - trace-aggregator: trace aggregator
                    to: imprime
                    type: default
                    config:
                      completion-expression: thisSignal.hasRoot()
                      inactive-timeout-millis: 15000
                  - tracelet: imprime
                    to: drop
                    type: baseProcessor
                    config:
                      action: |
                        println ""
                        println ""
                        println "dentro do aggregator ================"
                        println "traceId:" + thisSignal.traceIdEx
                        println "num spans:" + thisSignal.size
                        println "====================================="
                        println ""
                        println ""
                """;

        Siglet siglet = new Siglet(config);

        siglet.start();

    }
}
