package com.siglet.integrationtests.executions;

import com.siglet.camel.component.SigletComponent;
import com.siglet.config.item.ConfigItem;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.ConfigNode;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.BeforeEach;

import java.util.concurrent.CountDownLatch;

import static com.siglet.config.ConfigCheckFactory.globalConfigChecker;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ForkSpanletProcessor {

    public static void main(String[] args) throws Exception {

    ConfigParser configParser= new ConfigParser();


        var configFile = """
                receivers:
                - grpc: receiver
                  address: localhost:8080
                exporters:
                - grpc: first-exporter
                  address: localhost:4317
                - grpc: second-exporter
                  address: localhost:4444
                pipelines:
                - trace: pipeline
                  from: receiver
                  start:
                  - trace-aggregator
                  pipeline:
                  - trace-aggregator: trace-aggregator
                    to: router
                    type: default
                    config:
                      inactive-timeout-millis: 2000
                  - tracelet: router
                    to:
                    - first-exporter
                    - second-exporter
                    type: router
                    config:
                      default: first-exporter
                      routes:
                      - when: trace[0].spanId % 2 == 0
                        to: second-exporter
                """;


        ConfigNode node = configParser.parse(configFile);


        globalConfigChecker().check(node);

        Object conf = node.getValue();

        var globalConfig = assertInstanceOf(ConfigItem.class, conf);

        RouteBuilder b = globalConfig.build();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        try (CamelContext camelContext = new DefaultCamelContext()) {
            camelContext.addComponent("otelgrpc", new SigletComponent());
            camelContext.addRoutes(b);
            camelContext.start();
            Runtime.getRuntime().addShutdownHook(new Thread(countDownLatch::countDown));
            countDownLatch.await();
        }


    }
}
