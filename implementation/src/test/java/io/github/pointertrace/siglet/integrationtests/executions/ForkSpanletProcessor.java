package io.github.pointertrace.siglet.integrationtests.executions;

import io.github.pointertrace.siglet.impl.Siglet;

public class ForkSpanletProcessor {

    public static void main(String[] args) throws Exception {



        var configFile = """
                receivers:
                - grpc: receiverDescriptor
                  address: localhost:8080
                exporters:
                - grpc: first-exporterDescriptor
                  address: localhost:4317
                - grpc: second-exporterDescriptor
                  address: localhost:4444
                pipelineDescriptors:
                - trace: pipelineDescriptor
                  from: receiverDescriptor
                  start:
                  - trace-aggregator
                  pipelineDescriptor:
                  - trace-aggregator: trace-aggregator
                    to: router
                    type: default
                    config:
                      inactive-timeout-millis: 2000
                  - tracelet: router
                    to:
                    - first-exporterDescriptor
                    - second-exporterDescriptor
                    type: router
                    config:
                      default: first-exporterDescriptor
                      routeConfigs:
                      - when: trace[0].spanId % 2 == 0
                        to: second-exporterDescriptor
                """;


        Siglet siglet = new Siglet(configFile);

        siglet.start();


    }
}
