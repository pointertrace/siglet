package com.siglet.integrationtests.spanlet;

import com.siglet.config.Config;
import com.siglet.config.ConfigFactory;
import com.siglet.data.adapter.ProtoSpanAdapter;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class RouterSpanlet extends CamelTestSupport {

    @Test
    public void testSimple() throws Exception {


        String yaml = """
                receivers:
                - debug: receiver
                  address: direct:start
                exporters:
                - debug: first-exporter
                  address: mock:first-output
                - debug: second-exporter
                  address: mock:second-output
                - debug: third-exporter
                  address: mock:third-output
                pipelines:
                - trace: pipeline
                  from: receiver
                  start: spanlet
                  pipeline:
                  - spanlet: spanlet
                    to:
                    - first-exporter
                    - second-exporter
                    - third-exporter
                    type: router
                    config:
                      default: third-exporter
                      routes:
                      - when: span.name == "first"
                        to: first-exporter
                      - when: span.name == "second"
                        to: second-exporter
                """;

        ConfigFactory configFactory = new ConfigFactory();

        Config config = configFactory.create(yaml);

        context.addRoutes(config.getRouteBuilder());


        Span firstSpan = Span.newBuilder().setName("first").build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter = new ProtoSpanAdapter(firstSpan, resource, instrumentationScope, true);
        template.sendBody("direct:start", protoSpanAdapter);

        Span secondSpan = Span.newBuilder().setName("second").build();
        protoSpanAdapter = new ProtoSpanAdapter(secondSpan, resource, instrumentationScope, true);
        template.sendBody("direct:start", protoSpanAdapter);

        Span thirdSpan = Span.newBuilder().setName("third").build();
        protoSpanAdapter = new ProtoSpanAdapter(thirdSpan, resource, instrumentationScope, true);
        template.sendBody("direct:start", protoSpanAdapter);


        MockEndpoint mock = getMockEndpoint("mock:first-output");

        mock.expectedMessageCount(1);

        assertEquals(1, mock.getExchanges().size());
        var spanAdapter = assertInstanceOf(ProtoSpanAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
        assertEquals("first", spanAdapter.getName());

        mock = getMockEndpoint("mock:second-output");

        mock.expectedMessageCount(1);

        assertEquals(1, mock.getExchanges().size());
        spanAdapter = assertInstanceOf(ProtoSpanAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
        assertEquals("second", spanAdapter.getName());

        mock = getMockEndpoint("mock:third-output");

        mock.expectedMessageCount(1);

        assertEquals(1, mock.getExchanges().size());
        spanAdapter = assertInstanceOf(ProtoSpanAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
        assertEquals("third", spanAdapter.getName());
    }

}
