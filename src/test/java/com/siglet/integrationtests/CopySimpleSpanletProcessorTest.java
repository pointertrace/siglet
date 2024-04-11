package com.siglet.integrationtests;

import com.siglet.camel.component.SigletComponent;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.ConfigNode;
import com.siglet.data.adapter.ProtoSpanAdapter;
import com.siglet.spanlet.processor.GroovyProcessor;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.MulticastDefinition;
import org.apache.camel.model.RouteDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class CopySimpleSpanletProcessorTest {

    private ConfigParser configParser;

    @BeforeEach
    public void setUp() {
        configParser = new ConfigParser();
    }


    public void test() throws Exception {


        CountDownLatch countDownLatch = new CountDownLatch(1);
        CamelContext camelContext = new DefaultCamelContext();
        camelContext.addComponent("otelgrpc", new SigletComponent());
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                RouteDefinition from = from("otelgrpc:localhost:8080");
                from("direct:prefix")
                        .process(new GroovyProcessor("prefix", """
                                span.setName("prefix-" + span.getName())
                                println "prefix processor -----"
                                println "prefix spanId=" + span.getSpanId()
                                println "name=" + span.getName()
                                println "objid=" + span
                                println "-----"

                                """))
                        .to("otelgrpc:localhost:4444");
                from("direct:sufix")
                        .process(new GroovyProcessor("sufix", """
                                span.setName(span.getName() + "-sufix")
                                println "sufix processor -----"
                                println "spanId=" + span.getSpanId()
                                println "name=" + span.getName()
                                println "objid=" + span
                                println "-----"
                                """))
                        .to("otelgrpc:localhost:4317");

                MulticastDefinition fromMulticast = from.multicast();
                MulticastDefinition preparedMulticast = fromMulticast.onPrepare(new CloneProcessor("multicast"));
                preparedMulticast.to("direct:prefix");
                preparedMulticast.to("direct:sufix");
                preparedMulticast.end();
            }
        });
        camelContext.start();


        Runtime.getRuntime().addShutdownHook(new Thread(() -> countDownLatch.countDown()));

        countDownLatch.await();

    }

    public static class CloneProcessor implements Processor {

        private final String name;

        public CloneProcessor(String name) {
            this.name = name;
        }

        @Override
        public void process(Exchange exchange) throws Exception {
            ProtoSpanAdapter protoSpanAdapter = exchange.getIn().getBody(ProtoSpanAdapter.class);
            ProtoSpanAdapter cloned = protoSpanAdapter.clone();
            exchange.getIn().setBody(cloned);
            System.out.println("pre-processor (" + name + ")-------");
            System.out.println("original object=" + protoSpanAdapter);
            System.out.println("cloned object=" + cloned);
            System.out.println("-------(" + name + ")");
        }
    }
}
