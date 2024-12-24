package com.siglet.integrationtests.executions;

import com.siglet.camel.component.otelgrpc.SigletComponent;
import com.siglet.config.item.ConfigItem;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.concurrent.CountDownLatch;

import static com.siglet.config.ConfigCheckFactory.globalConfigChecker;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class SimpleMetricletProcessor {


    public static void main(String[] args) throws Exception {

        ConfigParser configParser = new ConfigParser();


        var configFile = """
                receivers:
                - grpc: receiver
                  address: localhost:4317
                  signal-type: metric
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
                    type: processor
                    config:
                      action: >
                        metric.setName("prefix-" + metric.getName())
                        println "metric-name="+ metric.getName()
                """;


        Node node = configParser.parse(configFile);


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

        camelContext.getEndpoints().forEach(e -> {
            System.out.println("endpoint:"+e.getEndpointUri());
            System.out.println("endpoint component:"+e.getComponent());
        });

        camelContext.getRoutes().forEach(r -> {
            System.out.println("rota:"+r.getId());
        });

        Runtime.getRuntime().addShutdownHook(new Thread(countDownLatch::countDown));

        countDownLatch.await();

    }
}
