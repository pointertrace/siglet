package com.siglet.integrationtests.tracelet;

import com.siglet.container.adapter.AdapterUtils;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.adapter.trace.ProtoTrace;
import com.siglet.container.config.Config;
import com.siglet.container.config.ConfigFactory;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class BaseEventloopProcessorEventloopTraceletTest {

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
                  start: tracelet
                  siglets:
                  - name: tracelet
                    kind: tracelet
                    to: exporter
                    type: groovy-action
                    config:
                      action: thisSignal.get(1).name = "prefix-" + thisSignal.get(1).name
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
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter(firstSpan, resource, instrumentationScope, true);

        Span secondSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(3, 4))
                .setSpanId(AdapterUtils.spanId(2))
                .setName("second-span")
                .build();

        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter(secondSpan, resource, instrumentationScope, true);

        ProtoTrace protoTraceAdapter = new ProtoTrace(protoSpanAdapter1, true);
        protoTraceAdapter.add(protoSpanAdapter2);

//        template.sendBody("direct:start",protoTraceAdapter);
//
//        MockEndpoint mock = getMockEndpoint("mock:output");
//
//        mock.expectedMessageCount(1);


//        assertEquals(1, mock.getExchanges().size());
//        var traceAdapter = assertInstanceOf(ProtoTrace.class, mock.getExchanges().getFirst().getIn().getBody());
//        assertEquals(2, traceAdapter.getSize());
//        assertEquals("prefix-first-span", traceAdapter.get(1).getName());
//        assertEquals("second-span", traceAdapter.get(2).getName());

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
                  start: tracelet
                  siglets:
                  - name: tracelet
                    kind: tracelet
                    to:
                    - first-exporter
                    - second-exporter
                    type: groovy-action
                    config:
                      action: thisSignal.get(1).name = "prefix-" + thisSignal.get(1).name
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
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter(firstSpan, resource, instrumentationScope, true);

        Span secondSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(3, 4))
                .setSpanId(AdapterUtils.spanId(2))
                .setName("second-span")
                .build();

        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter(secondSpan, resource, instrumentationScope, true);

        ProtoTrace protoTraceAdapter = new ProtoTrace(protoSpanAdapter1, true);
        protoTraceAdapter.add(protoSpanAdapter2);

//        template.sendBody("direct:start",protoTraceAdapter);
//
//        MockEndpoint mock = getMockEndpoint("mock:first-output");
//
//        mock.expectedMessageCount(1);


//        assertEquals(1, mock.getExchanges().size());
//        var traceAdapter = assertInstanceOf(ProtoTrace.class, mock.getExchanges().getFirst().getIn().getBody());
//        assertEquals(2, traceAdapter.getSize());
//        assertEquals("prefix-first-span", traceAdapter.get(1).getName());
//        assertEquals("second-span", traceAdapter.get(2).getName());
//
//
//
//        mock = getMockEndpoint("mock:second-output");
//        mock.expectedMessageCount(1);
//
//
//        assertEquals(1, mock.getExchanges().size());
//        traceAdapter = assertInstanceOf(ProtoTrace.class, mock.getExchanges().getFirst().getIn().getBody());
//        assertEquals(2, traceAdapter.getSize());
//        assertEquals("prefix-first-span", traceAdapter.get(1).getName());
//        assertEquals("second-span", traceAdapter.get(2).getName());
    }

}
