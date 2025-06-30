package com.siglet.integrationtests.traceaggregator;

import com.siglet.container.adapter.AdapterUtils;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.config.Config;
import com.siglet.container.config.ConfigFactory;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class TraceAggregatorTest {


    @Test
    void testSimple() throws Exception {


        String yaml = """
                receivers:
                - debug: receiver
                  address: direct:start
                exporters:
                - debug: exporter
                  address: mock:output
                pipelines:
                - name: pipeline
                  signal: trace
                  from: receiver
                  start: trace-aggregator
                  siglets:
                    - name: trace-aggregator
                      kind: trace-aggregator
                      to: exporter
                      type: default
                      config:
                        timeout-millis: 1000
                        inactive-timeout-millis: 2000
                        completion-expression: return thisSignal.size == 2
                """;

        ConfigFactory configFactory = new ConfigFactory();

        Config config = configFactory.create(yaml);

//        context.addRoutes(config.getRouteBuilder());


        Span firstSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(3, 4))
                .setSpanId(AdapterUtils.spanId(1))
                .setName("first-span")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter().recycle(firstSpan, resource, instrumentationScope);
//        template.sendBody("direct:start", protoSpanAdapter1);

        Span secondSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(3, 4))
                .setSpanId(AdapterUtils.spanId(2))
                .setName("second-span")
                .build();
        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter().recycle(secondSpan, resource, instrumentationScope);
//        template.sendBody("direct:start", protoSpanAdapter2);
//
//        MockEndpoint mock = getMockEndpoint("mock:output");
//        mock.expectedMessageCount(1);
//        mock.assertIsSatisfied();
//        mock.expectedMessageCount(1);
//
//
//        assertEquals(1, mock.getExchanges().size());
//        var traceAdapter = assertInstanceOf(ProtoTrace.class, mock.getExchanges().getFirst().getIn().getBody());
//        assertEquals(2, traceAdapter.getSize());

    }

    @Test
    void testMultiple() throws Exception {


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
                - name: pipeline
                  signal: trace
                  from: receiver
                  start: trace-aggregator
                  siglets:
                    - name: trace-aggregator
                      kind: trace-aggregator
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

//        context.addRoutes(config.getRouteBuilder());


        Span firstSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(3, 4))
                .setSpanId(AdapterUtils.spanId(1))
                .setName("first-span")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter().recycle(firstSpan, resource, instrumentationScope);
//        template.sendBody("direct:start", protoSpanAdapter1);

        Span secondSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(3, 4))
                .setSpanId(AdapterUtils.spanId(2))
                .setName("second-span")
                .build();
        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter().recycle(secondSpan, resource, instrumentationScope);
//        template.sendBody("direct:start", protoSpanAdapter2);


//        MockEndpoint mock = getMockEndpoint("mock:first-output");
//        mock.expectedMessageCount(1);
//        mock.assertIsSatisfied();
//
//        assertEquals(1, mock.getExchanges().size());
//        var traceAdapter = assertInstanceOf(ProtoTrace.class, mock.getExchanges().getFirst().getIn().getBody());
//        assertEquals(2, traceAdapter.getSize());
//        assertNotNull(traceAdapter.get(1));
//        assertNotNull(traceAdapter.get(2));


//        mock = getMockEndpoint("mock:second-output");
//        mock.expectedMessageCount(1);
//        mock.assertIsSatisfied();
//
//        assertEquals(1, mock.getExchanges().size());
//        traceAdapter = assertInstanceOf(ProtoTrace.class, mock.getExchanges().getFirst().getIn().getBody());
//        assertEquals(2, traceAdapter.getSize());
//        assertNotNull(traceAdapter.get(1));
//        assertNotNull(traceAdapter.get(2));
    }
}
