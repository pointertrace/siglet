package com.siglet.integrationtests;

import com.siglet.camel.component.SigletComponent;
import com.siglet.config.item.ConfigItem;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.ConfigNode;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static com.siglet.config.ConfigCheckFactory.globalConfigChecker;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ForkSpanletProcessorTest {

    private ConfigParser configParser;

    @BeforeEach
    public void setUp() {
        configParser = new ConfigParser();
    }


    public void test() throws Exception {
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
                  - first spanlet
                  - second spanlet
                  pipeline:
                  - spanlet: first spanlet
                    to: first-exporter
                    type: processor
                    config:
                      action: >
                        span.setName("prefix-" + span.getName())
                        println "first spanId=" + span.getSpanId()
                  - spanlet: second spanlet
                    to: second-exporter
                    type: processor
                    config:
                      action: >
                        span.setName(span.getName() + "-sufix")
                        println "second spanId=" + span.getSpanId()
                """;


        ConfigNode node = configParser.parse(configFile);


        globalConfigChecker().check(node);

        Object conf = node.getValue();

        var globalConfig = assertInstanceOf(ConfigItem.class, conf);

        globalConfig.validateUniqueNames();
        RouteBuilder b = globalConfig.build();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        try (CamelContext camelContext = new DefaultCamelContext()) {
            camelContext.addComponent("otelgrpc", new SigletComponent());
            camelContext.addRoutes(b);
            camelContext.start();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> countDownLatch.countDown() ));

        countDownLatch.await();

    }
}
