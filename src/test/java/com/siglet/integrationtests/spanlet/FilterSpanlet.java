package com.siglet.integrationtests.spanlet;

import com.siglet.config.Config;
import com.siglet.config.ConfigFactory;
import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class FilterSpanlet extends CamelTestSupport {

    @Test
    public void testSimple() throws Exception {


        String yaml = """
                receivers:
                - debug: receiver
                  address: direct:start
                exporters:
                - debug: exporter
                  address: mock:output
                pipelines:
                - trace: pipeline
                  from: receiver
                  start: spanlet
                  pipeline:
                  - spanlet: spanlet
                    to: exporter
                    type: filter
                    config:
                      expression: span.name.startsWith("prefix")
                """;

        ConfigFactory configFactory = new ConfigFactory();

        Config config = configFactory.create(yaml);

        context.addRoutes(config.getRouteBuilder());


        Span firstSpan = Span.newBuilder().setName("prefix-span-name").build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter(firstSpan, resource, instrumentationScope, true);
        template.sendBody("direct:start", protoSpanAdapter1);

        Span secondSpan = Span.newBuilder().setName("span-name").build();
        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter(secondSpan, resource, instrumentationScope, true);
        template.sendBody("direct:start", protoSpanAdapter2);

        MockEndpoint mock = getMockEndpoint("mock:output");

        mock.expectedMessageCount(1);


        assertEquals(1, mock.getExchanges().size());
        var spanAdapter = assertInstanceOf(ProtoSpanAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
        assertEquals("prefix-span-name", spanAdapter.getName());

    }

    @Test
    public void testMultiple() throws Exception {


        String yaml = """
                receivers:
                - debug: receiver
                  address: direct:start
                exporters:
                - debug: first-exporter
                  address: mock:first-output
                - debug: second-exporter
                  address: mock:second-output
                pipelines:
                - trace: pipeline
                  from: receiver
                  start: spanlet
                  pipeline:
                  - spanlet: spanlet
                    to:
                    - first-exporter
                    - second-exporter
                    type: filter
                    config:
                      expression: span.name.startsWith("prefix")
                """;

        ConfigFactory configFactory = new ConfigFactory();

        Config config = configFactory.create(yaml);

        context.addRoutes(config.getRouteBuilder());


        Span firstSpan = Span.newBuilder().setName("prefix-span-name").build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter(firstSpan, resource, instrumentationScope, true);
        template.sendBody("direct:start", protoSpanAdapter1);

        Span secondSpan = Span.newBuilder().setName("span-name").build();
        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter(secondSpan, resource, instrumentationScope, true);
        template.sendBody("direct:start", protoSpanAdapter2);

        // first output
        MockEndpoint mock = getMockEndpoint("mock:first-output");

        mock.expectedMessageCount(1);


        assertEquals(1, mock.getExchanges().size());
        var spanAdapter = assertInstanceOf(ProtoSpanAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
        assertEquals("prefix-span-name", spanAdapter.getName());


        // second output
        mock = getMockEndpoint("mock:second-output");

        mock.expectedMessageCount(1);


        assertEquals(1, mock.getExchanges().size());
        spanAdapter = assertInstanceOf(ProtoSpanAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
        assertEquals("prefix-span-name", spanAdapter.getName());
    }
}
