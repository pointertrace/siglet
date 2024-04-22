package com.siglet.integrationtests.tracelet;

import com.google.protobuf.ByteString;
import com.siglet.config.Config;
import com.siglet.config.ConfigFactory;
import com.siglet.data.adapter.AdapterUtils;
import com.siglet.data.adapter.ProtoSpanAdapter;
import com.siglet.data.adapter.ProtoTraceAdapter;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ProcessorTracelet extends CamelTestSupport {

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
                  start: tracelet
                  pipeline:
                  - tracelet: tracelet
                    to: exporter
                    type: processor
                    config:
                      action: trace.get(1).name = "prefix-" + trace.get(1).name
                """;

        ConfigFactory configFactory = new ConfigFactory();

        Config config = configFactory.otherCreate(yaml);

        context.addRoutes(config.getRouteBuilder());


        Span firstSpan = Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(3, 4)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(1)))
                .setName("first-span")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter(firstSpan, resource, instrumentationScope, true);

        Span secondSpan = Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(3, 4)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(2)))
                .setName("second-span")
                .build();

        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter(secondSpan, resource, instrumentationScope, true);

        ProtoTraceAdapter protoTraceAdapter = new ProtoTraceAdapter(protoSpanAdapter1, true);
        protoTraceAdapter.add(protoSpanAdapter2);

        template.sendBody("direct:start",protoTraceAdapter);

        MockEndpoint mock = getMockEndpoint("mock:output");

        mock.expectedMessageCount(1);


        assertEquals(1, mock.getExchanges().size());
        var traceAdapter = assertInstanceOf(ProtoTraceAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
        assertEquals(2, traceAdapter.getSize());
        assertEquals("prefix-first-span", traceAdapter.get(1).getName());
        assertEquals("second-span", traceAdapter.get(2).getName());

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
                  start: tracelet
                  pipeline:
                  - tracelet: tracelet
                    to:
                    - first-exporter
                    - second-exporter
                    type: processor
                    config:
                      action: trace.get(1).name = "prefix-" + trace.get(1).name
                """;

        ConfigFactory configFactory = new ConfigFactory();

        Config config = configFactory.otherCreate(yaml);

        context.addRoutes(config.getRouteBuilder());


        Span firstSpan = Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(3, 4)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(1)))
                .setName("first-span")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter(firstSpan, resource, instrumentationScope, true);

        Span secondSpan = Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(3, 4)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(2)))
                .setName("second-span")
                .build();

        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter(secondSpan, resource, instrumentationScope, true);

        ProtoTraceAdapter protoTraceAdapter = new ProtoTraceAdapter(protoSpanAdapter1, true);
        protoTraceAdapter.add(protoSpanAdapter2);

        template.sendBody("direct:start",protoTraceAdapter);

        MockEndpoint mock = getMockEndpoint("mock:first-output");

        mock.expectedMessageCount(1);


        assertEquals(1, mock.getExchanges().size());
        var traceAdapter = assertInstanceOf(ProtoTraceAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
        assertEquals(2, traceAdapter.getSize());
        assertEquals("prefix-first-span", traceAdapter.get(1).getName());
        assertEquals("second-span", traceAdapter.get(2).getName());



        mock = getMockEndpoint("mock:second-output");
        mock.expectedMessageCount(1);


        assertEquals(1, mock.getExchanges().size());
        traceAdapter = assertInstanceOf(ProtoTraceAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
        assertEquals(2, traceAdapter.getSize());
        assertEquals("prefix-first-span", traceAdapter.get(1).getName());
        assertEquals("second-span", traceAdapter.get(2).getName());
    }

}
