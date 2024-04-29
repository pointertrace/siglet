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

import static org.junit.jupiter.api.Assertions.*;

public class RouterTracelet extends CamelTestSupport {

    @Test
    public void testSimple_firstRoute() throws Exception {


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
                  start: tracelet
                  pipeline:
                  - tracelet: tracelet
                    to:
                    - first-exporter
                    - second-exporter
                    - third-exporter
                    type: router
                    config:
                      default: third-exporter
                      routes:
                      - when: trace.get(1).name == "first-span"
                        to: first-exporter
                      - when: trace.get(1).name == "second-span"
                        to: second-exporter
                """;

        ConfigFactory configFactory = new ConfigFactory();

        Config config = configFactory.create(yaml);

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

        template.sendBody("direct:start", protoTraceAdapter);

        MockEndpoint mock = getMockEndpoint("mock:first-output");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

        assertEquals(1, mock.getExchanges().size());
        var traceAdapter = assertInstanceOf(ProtoTraceAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
        assertEquals(2, traceAdapter.getSize());
        assertNotNull(traceAdapter.get(1));
        assertNotNull(traceAdapter.get(2));

        mock = getMockEndpoint("mock:second-output");
        mock.expectedMessageCount(0);
        mock.assertIsSatisfied();

        mock = getMockEndpoint("mock:third-output");
        mock.expectedMessageCount(0);
        mock.assertIsSatisfied();
    }

    @Test
    public void testSimple_scondRoute() throws Exception {


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
                  start: tracelet
                  pipeline:
                  - tracelet: tracelet
                    to:
                    - first-exporter
                    - second-exporter
                    - third-exporter
                    type: router
                    config:
                      default: third-exporter
                      routes:
                      - when: trace.get(1).name == "first-span"
                        to: first-exporter
                      - when: trace.get(1).name == "second-span"
                        to: second-exporter
                """;

        ConfigFactory configFactory = new ConfigFactory();

        Config config = configFactory.create(yaml);

        context.addRoutes(config.getRouteBuilder());


        Span firstSpan = Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(3, 4)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(1)))
                .setName("second-span")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter(firstSpan, resource, instrumentationScope, true);

        Span secondSpan = Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(3, 4)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(2)))
                .setName("first-span")
                .build();
        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter(secondSpan, resource, instrumentationScope, true);

        ProtoTraceAdapter protoTraceAdapter = new ProtoTraceAdapter(protoSpanAdapter1, true);
        protoTraceAdapter.add(protoSpanAdapter2);

        template.sendBody("direct:start", protoTraceAdapter);

        MockEndpoint mock = getMockEndpoint("mock:first-output");
        mock.expectedMessageCount(0);
        mock.assertIsSatisfied();

        mock = getMockEndpoint("mock:second-output");
        assertEquals(1, mock.getExchanges().size());
        var traceAdapter = assertInstanceOf(ProtoTraceAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
        assertEquals(2, traceAdapter.getSize());
        assertNotNull(traceAdapter.get(1));
        assertNotNull(traceAdapter.get(2));

        mock = getMockEndpoint("mock:third-output");
        mock.expectedMessageCount(0);
        mock.assertIsSatisfied();
    }

    @Test
    public void testSimple_thirdRoute() throws Exception {


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
                  start: tracelet
                  pipeline:
                  - tracelet: tracelet
                    to:
                    - first-exporter
                    - second-exporter
                    - third-exporter
                    type: router
                    config:
                      default: third-exporter
                      routes:
                      - when: trace.get(1).name == "first-span"
                        to: first-exporter
                      - when: trace.get(1).name == "second-span"
                        to: second-exporter
                """;

        ConfigFactory configFactory = new ConfigFactory();

        Config config = configFactory.create(yaml);

        context.addRoutes(config.getRouteBuilder());


        Span firstSpan = Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(3, 4)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(1)))
                .setName("other-span")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter(firstSpan, resource, instrumentationScope, true);

        Span secondSpan = Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(3, 4)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(2)))
                .setName("first-span")
                .build();
        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter(secondSpan, resource, instrumentationScope, true);

        ProtoTraceAdapter protoTraceAdapter = new ProtoTraceAdapter(protoSpanAdapter1, true);
        protoTraceAdapter.add(protoSpanAdapter2);

        template.sendBody("direct:start", protoTraceAdapter);

        MockEndpoint mock = getMockEndpoint("mock:first-output");
        mock.expectedMessageCount(0);
        mock.assertIsSatisfied();

        mock = getMockEndpoint("mock:second-output");
        mock.expectedMessageCount(0);
        mock.assertIsSatisfied();

        mock = getMockEndpoint("mock:third-output");
        assertEquals(1, mock.getExchanges().size());
        var traceAdapter = assertInstanceOf(ProtoTraceAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
        assertEquals(2, traceAdapter.getSize());
        assertNotNull(traceAdapter.get(1));
        assertNotNull(traceAdapter.get(2));
    }
}
