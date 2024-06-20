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

public class SimpleSpanletProcessorTest {

    private ConfigParser configParser;

    @BeforeEach
    public void setUp() {
        configParser = new ConfigParser();
    }


    @Test
    public void test() throws Exception {
        var configFile = """
                receivers:
                - grpc: receiver_1
                  address: localhost:8080
                - grpc: receiver_2
                  address: localhost:8081
                exporters:
                - grpc: exporter
                  address: localhost:4317
                pipelines:
                - trace: simple pipeline
                  from: receiver_1
                  start: first spanlet
                  pipeline:
                  - spanlet: first spanlet
                    to: second spanlet
                    type: processor
                    config:
                      action: >
                        span.setName("prefix-" + span.getName())
                        println "first spanId=" + span.getSpanId()
                  - spanlet: second spanlet
                    to: exporter
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

        globalConfig.build();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        CamelContext camelContext = new DefaultCamelContext();
        camelContext.addComponent("otelgrpc", new SigletComponent());
        RouteBuilder routeBuilder = globalConfig.build();
         routeBuilder.getRoutes().getRoutes().forEach(rd -> {
             System.out.println(rd.toString());
        });
        camelContext.addRoutes(routeBuilder);
        camelContext.start();


        Runtime.getRuntime().addShutdownHook(new Thread(() -> countDownLatch.countDown() ));

        countDownLatch.await();

    }
}
