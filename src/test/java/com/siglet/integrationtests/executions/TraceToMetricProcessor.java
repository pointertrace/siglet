package com.siglet.integrationtests.executions;

import com.siglet.camel.component.SigletComponent;
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


        ConfigParser configParser = new ConfigParser();


        var configFile = """
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
                        println "span spanId=" + span.getSpanId()
                        newMetric = signalCreator.newMetric()
                        newMetric.setName("gauge example novo")
                                 .setDescription("gauge example description")
                                 .setUnit("gauge example unit")
                                 .gauge()
                                 .getDataPoints().add()
                                 .setTimeUnixNano(1)
                                 .setAsLong(100)
                                 .getAttributes()
                                 .set("attribute", "attribute value");
                
                        sender.send("otelgrpc://localhost:8080?signalType=metric",newMetric)
                - metric: metric-pipeline
                  from: metric-receiver
                  start: metriclet
                  pipeline:
                  - metriclet: metriclet
                    to: exporter
                    type: processor
                    config:
                      action: |
                        println "metric name=" + metric.getName()
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


        Runtime.getRuntime().addShutdownHook(new Thread(() -> countDownLatch.countDown()));

        countDownLatch.await();

    }
}
