package com.siglet.integrationtests.tracelet;

import com.siglet.container.Siglet;
import com.siglet.container.adapter.AdapterUtils;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.adapter.trace.ProtoTrace;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class RouterTraceletTest {

    @Test
    void testSimple_firstRoute() throws Exception {


        String yaml = """
                receivers:
                - debug: receiver
                exporters:
                - debug: first-exporter
                - debug: second-exporter
                - debug: third-exporter
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
                    - third-exporter
                    type: groovy-router
                    config:
                      default: third-exporter
                      routeConfigs:
                      - when: thisSignal.get(1).name == "first-span"
                        to: first-exporter
                      - when: thisSignal.get(1).name == "second-span"
                        to: second-exporter
                """;

        Siglet siglet = new Siglet(yaml);

        siglet.start();

        Span firstSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(3, 4))
                .setSpanId(AdapterUtils.spanId(1))
                .setName("first-span")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter().recycle(firstSpan, resource, instrumentationScope);

        Span secondSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(3, 4))
                .setSpanId(AdapterUtils.spanId(2))
                .setName("second-span")
                .build();
        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter().recycle(secondSpan, resource, instrumentationScope);

        ProtoTrace protoTraceAdapter = new ProtoTrace(protoSpanAdapter1, true);
        protoTraceAdapter.add(protoSpanAdapter2);

//        template.sendBody("direct:start", protoTraceAdapter);
//
//        MockEndpoint mock = getMockEndpoint("mock:first-output");
//        mock.expectedMessageCount(1);
//        mock.assertIsSatisfied();
//
//        assertEquals(1, mock.getExchanges().size());
//        var traceAdapter = assertInstanceOf(ProtoTrace.class, mock.getExchanges().getFirst().getIn().getBody());
//        assertEquals(2, traceAdapter.getSize());
//        assertNotNull(traceAdapter.get(1));
//        assertNotNull(traceAdapter.get(2));
//
//        mock = getMockEndpoint("mock:second-output");
//        mock.expectedMessageCount(0);
//        mock.assertIsSatisfied();
//
//        mock = getMockEndpoint("mock:third-output");
//        mock.expectedMessageCount(0);
//        mock.assertIsSatisfied();
    }

    @Test
    void testSimple_scondRoute() throws Exception {


        String yaml = """
                receivers:
                - debug: receiver
                exporters:
                - debug: first-exporter
                - debug: second-exporter
                - debug: third-exporter
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
                    - third-exporter
                    type: groovy-router
                    config:
                      default: third-exporter
                      routes:
                      - when: thisSignal.get(1).name == "first-span"
                        to: first-exporter
                      - when: thisSignal.get(1).name == "second-span"
                        to: second-exporter
                """;

        Siglet siglet = new Siglet(yaml);
        siglet.start();


        Span firstSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(3, 4))
                .setSpanId(AdapterUtils.spanId(1))
                .setName("second-span")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter().recycle(firstSpan, resource, instrumentationScope);

        Span secondSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(3, 4))
                .setSpanId(AdapterUtils.spanId(2))
                .setName("first-span")
                .build();
        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter().recycle(secondSpan, resource, instrumentationScope);

        ProtoTrace protoTraceAdapter = new ProtoTrace(protoSpanAdapter1, true);
        protoTraceAdapter.add(protoSpanAdapter2);

//        template.sendBody("direct:start", protoTraceAdapter);
//
//        MockEndpoint mock = getMockEndpoint("mock:first-output");
//        mock.expectedMessageCount(0);
//        mock.assertIsSatisfied();
//
//        mock = getMockEndpoint("mock:second-output");
//        assertEquals(1, mock.getExchanges().size());
//        var traceAdapter = assertInstanceOf(ProtoTrace.class, mock.getExchanges().getFirst().getIn().getBody());
//        assertEquals(2, traceAdapter.getSize());
//        assertNotNull(traceAdapter.get(1));
//        assertNotNull(traceAdapter.get(2));
//
//        mock = getMockEndpoint("mock:third-output");
//        mock.expectedMessageCount(0);
//        mock.assertIsSatisfied();
    }

    @Test
    void testSimple_thirdRoute() throws Exception {


        String yaml = """
                receivers:
                - debug: receiver
                exporters:
                - debug: first-exporter
                - debug: second-exporter
                - debug: third-exporter
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
                    - third-exporter
                    type: groovy-router
                    config:
                      default: third-exporter
                      routes:
                      - when: thisSignal.get(1).name == "first-span"
                        to: first-exporter
                      - when: thisSignal.get(1).name == "second-span"
                        to: second-exporter
                """;

        Siglet siglet = new Siglet(yaml);
        siglet.start();


        Span firstSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(3, 4))
                .setSpanId(AdapterUtils.spanId(1))
                .setName("other-span")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter().recycle(firstSpan, resource, instrumentationScope);
        Span secondSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(3, 4))
                .setSpanId(AdapterUtils.spanId(2))
                .setName("first-span")
                .build();
        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter().recycle(secondSpan, resource, instrumentationScope);

        ProtoTrace protoTraceAdapter = new ProtoTrace(protoSpanAdapter1, true);
        protoTraceAdapter.add(protoSpanAdapter2);

//        template.sendBody("direct:start", protoTraceAdapter);
//
//        MockEndpoint mock = getMockEndpoint("mock:first-output");
//        mock.expectedMessageCount(0);
//        mock.assertIsSatisfied();
//
//        mock = getMockEndpoint("mock:second-output");
//        mock.expectedMessageCount(0);
//        mock.assertIsSatisfied();
//
//        mock = getMockEndpoint("mock:third-output");
//        assertEquals(1, mock.getExchanges().size());
//        var traceAdapter = assertInstanceOf(ProtoTrace.class, mock.getExchanges().getFirst().getIn().getBody());
//        assertEquals(2, traceAdapter.getSize());
//        assertNotNull(traceAdapter.get(1));
//        assertNotNull(traceAdapter.get(2));
    }
}
