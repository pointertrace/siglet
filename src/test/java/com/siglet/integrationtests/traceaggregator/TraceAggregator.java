package com.siglet.integrationtests.traceaggregator;

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

import static org.junit.jupiter.api.Assertions.*;

public class TraceAggregator extends CamelTestSupport {


    @Test
    public void testSimpl() throws Exception {


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
                  start: trace-aggregator
                  pipeline:
                    - trace-aggregator: trace-aggregator
                      to: exporter
                      type: default
                      config:
                        timeout-millis: 1000
                        inactive-timeout-millis: 2000
                        completion-expression: return thisSignal.size == 2
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
        template.sendBody("direct:start", protoSpanAdapter1);

        Span secondSpan = Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(3, 4)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(2)))
                .setName("second-span")
                .build();
        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter(secondSpan, resource, instrumentationScope, true);
        template.sendBody("direct:start", protoSpanAdapter2);

        MockEndpoint mock = getMockEndpoint("mock:output");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
        mock.expectedMessageCount(1);


        assertEquals(1, mock.getExchanges().size());
        var traceAdapter = assertInstanceOf(ProtoTraceAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
        assertEquals(2, traceAdapter.getSize());

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
                  start: trace-aggregator
                  pipeline:
                    - trace-aggregator: trace-aggregator
                      to:
                      - first-exporter
                      - second-exporter
                      type: default
                      config:
                        timeout-millis: 1000
                        inactive-timeout-millis: 2000
                        completion-expression: return thisSignal.size == 2
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
        template.sendBody("direct:start", protoSpanAdapter1);

        Span secondSpan = Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(3, 4)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(2)))
                .setName("second-span")
                .build();
        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter(secondSpan, resource, instrumentationScope, true);
        template.sendBody("direct:start", protoSpanAdapter2);


        MockEndpoint mock = getMockEndpoint("mock:first-output");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

        assertEquals(1, mock.getExchanges().size());
        var traceAdapter = assertInstanceOf(ProtoTraceAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
        assertEquals(2, traceAdapter.getSize());
        assertNotNull(traceAdapter.get(1));
        assertNotNull(traceAdapter.get(2));


        mock = getMockEndpoint("mock:second-output");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

        assertEquals(1, mock.getExchanges().size());
        traceAdapter = assertInstanceOf(ProtoTraceAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
        assertEquals(2, traceAdapter.getSize());
        assertNotNull(traceAdapter.get(1));
        assertNotNull(traceAdapter.get(2));
    }
}
