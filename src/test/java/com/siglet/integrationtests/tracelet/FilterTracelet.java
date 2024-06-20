package com.siglet.integrationtests.tracelet;

import com.google.protobuf.ByteString;
import com.siglet.config.Config;
import com.siglet.config.ConfigFactory;
import com.siglet.data.adapter.AdapterUtils;
import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import com.siglet.data.adapter.trace.ProtoTraceAdapter;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class FilterTracelet extends CamelTestSupport {

    @Test
    public void testSimple_passFilter() throws Exception {


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
                    type: filter
                    config:
                      expression: >
                        trace.get(1).name.startsWith("prefix")
                """;

        ConfigFactory configFactory = new ConfigFactory();

        Config config = configFactory.create(yaml);

        context.addRoutes(config.getRouteBuilder());


        Span firstSpan = Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(3, 4)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(1)))
                .setName("prefixed-span")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter(firstSpan, resource, instrumentationScope, true);

        Span secondSpan = Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(3, 4)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(2)))
                .setName("non-prefixed-span")
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

    }

    @Test
    public void testSimple_nonPassFilter() throws Exception {


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
                    type: filter
                    config:
                      expression: >
                        trace.get(1).name.startsWith("prefix")
                """;

        ConfigFactory configFactory = new ConfigFactory();

        Config config = configFactory.create(yaml);

        context.addRoutes(config.getRouteBuilder());


        Span firstSpan = Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(3, 4)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(1)))
                .setName("non-prefixed-span")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter(firstSpan, resource, instrumentationScope, true);

        Span secondSpan = Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(3, 4)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(2)))
                .setName("non-prefixed-span")
                .build();

        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter(secondSpan, resource, instrumentationScope, true);

        ProtoTraceAdapter protoTraceAdapter = new ProtoTraceAdapter(protoSpanAdapter1, true);
        protoTraceAdapter.add(protoSpanAdapter2);

        template.sendBody("direct:start",protoTraceAdapter);

        MockEndpoint mock = getMockEndpoint("mock:output");

        mock.expectedMessageCount(0);


    }



    @Test
    public void testMultiple_passFilter() throws Exception {


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
                    type: filter
                    config:
                      expression: >
                        trace.get(1).name.startsWith("prefix")
                """;

        ConfigFactory configFactory = new ConfigFactory();

        Config config = configFactory.create(yaml);

        context.addRoutes(config.getRouteBuilder());


        Span firstSpan = Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(3, 4)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(1)))
                .setName("prefixed-span")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter(firstSpan, resource, instrumentationScope, true);

        Span secondSpan = Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(3, 4)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(2)))
                .setName("non-prefixed-span")
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

        mock = getMockEndpoint("mock:second-output");

        mock.expectedMessageCount(1);


        assertEquals(1, mock.getExchanges().size());
        traceAdapter = assertInstanceOf(ProtoTraceAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
        assertEquals(2, traceAdapter.getSize());
    }

    @Test
    public void testMultiple_nonPassFilter() throws Exception {


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
                    type: filter
                    config:
                      expression: >
                        trace.get(1).name.startsWith("prefix")
                """;

        ConfigFactory configFactory = new ConfigFactory();

        Config config = configFactory.create(yaml);

        context.addRoutes(config.getRouteBuilder());


        Span firstSpan = Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(3, 4)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(1)))
                .setName("non-prefixed-span")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter(firstSpan, resource, instrumentationScope, true);

        Span secondSpan = Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(3, 4)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(2)))
                .setName("non-prefixed-span")
                .build();

        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter(secondSpan, resource, instrumentationScope, true);

        ProtoTraceAdapter protoTraceAdapter = new ProtoTraceAdapter(protoSpanAdapter1, true);
        protoTraceAdapter.add(protoSpanAdapter2);

        template.sendBody("direct:start",protoTraceAdapter);

        MockEndpoint mock = getMockEndpoint("mock:first-output");

        mock.expectedMessageCount(0);

        mock = getMockEndpoint("mock:second-output");

        mock.expectedMessageCount(0);

    }

}
