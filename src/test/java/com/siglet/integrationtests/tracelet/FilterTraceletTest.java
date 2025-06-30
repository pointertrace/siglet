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
class FilterTraceletTest {

    @Test
    void testSimple_passFilter() throws Exception {


        String yaml = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: pipeline
                  signal: trace
                  from: receiver
                  start: tracelet
                  siglets:
                  - name: tracelet
                    kind: tracelet
                    to: exporter
                    type: groovy-filter
                    config:
                      expression: >
                        thisSignal.get(1).name.startsWith("prefix")
                """;

        Siglet siglet = new Siglet(yaml);

        siglet.start();


        Span firstSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(3, 4))
                .setSpanId(AdapterUtils.spanId(1))
                .setName("prefixed-span")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter().recycle(firstSpan, resource, instrumentationScope);

        Span secondSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(3, 4))
                .setSpanId(AdapterUtils.spanId(2))
                .setName("non-prefixed-span")
                .build();

        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter().recycle(secondSpan, resource, instrumentationScope);

        ProtoTrace protoTraceAdapter = new ProtoTrace(protoSpanAdapter1, true);
        protoTraceAdapter.add(protoSpanAdapter2);




//        assertEquals(1, mock.getExchanges().size());
//        var traceAdapter = assertInstanceOf(ProtoTrace.class, mock.getExchanges().getFirst().getIn().getBody());
//        assertEquals(2, traceAdapter.getSize());

    }

    @Test
    void testSimple_nonPassFilter() throws Exception {


        String yaml = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: pipeline
                  signal: trace
                  from: receiver
                  start: tracelet
                  siglets:
                  - name: tracelet
                    kind: tracelet
                    to: exporter
                    type: groovy-filter
                    config:
                      expression: >
                        thisSignal.get(1).name.startsWith("prefix")
                """;

        Siglet siglet = new Siglet(yaml);

        Span firstSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(3, 4))
                .setSpanId(AdapterUtils.spanId(1))
                .setName("non-prefixed-span")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter().recycle(firstSpan, resource, instrumentationScope);

        Span secondSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(3, 4))
                .setSpanId(AdapterUtils.spanId(2))
                .setName("non-prefixed-span")
                .build();

        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter().recycle(secondSpan, resource, instrumentationScope);

        ProtoTrace protoTraceAdapter = new ProtoTrace(protoSpanAdapter1, true);
        protoTraceAdapter.add(protoSpanAdapter2);



    }



    @Test
    void testMultiple_passFilter() throws Exception {


        String yaml = """
                receivers:
                - debug: receiver
                exporters:
                - debug: first-exporter
                - debug: second-exporter
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
                    type: groovy-filter
                    config:
                      expression: >
                        thisSignal.get(1).name.startsWith("prefix")
                """;

        Siglet siglet = new Siglet(yaml);


        Span firstSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(3, 4))
                .setSpanId(AdapterUtils.spanId(1))
                .setName("prefixed-span")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter().recycle(firstSpan, resource, instrumentationScope);

        Span secondSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(3, 4))
                .setSpanId(AdapterUtils.spanId(2))
                .setName("non-prefixed-span")
                .build();

        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter().recycle(secondSpan, resource, instrumentationScope);

        ProtoTrace protoTraceAdapter = new ProtoTrace(protoSpanAdapter1, true);
        protoTraceAdapter.add(protoSpanAdapter2);



//        assertEquals(1, mock.getExchanges().size());
//        var traceAdapter = assertInstanceOf(ProtoTrace.class, mock.getExchanges().getFirst().getIn().getBody());
//        assertEquals(2, traceAdapter.getSize());
//
//        mock = getMockEndpoint("mock:second-output");
//
//        mock.expectedMessageCount(1);
//
//
//        assertEquals(1, mock.getExchanges().size());
//        traceAdapter = assertInstanceOf(ProtoTrace.class, mock.getExchanges().getFirst().getIn().getBody());
//        assertEquals(2, traceAdapter.getSize());
    }

    @Test
    void testMultiple_nonPassFilter() throws Exception {


        String yaml = """
                receivers:
                - debug: receiver
                exporters:
                - debug: first-exporter
                - debug: second-exporter
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
                    type: groovy-filter
                    config:
                      expression: >
                        thisSignal.get(1).name.startsWith("prefix")
                """;

        Siglet siglet = new Siglet(yaml);

        Span firstSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(3, 4))
                .setSpanId(AdapterUtils.spanId(1))
                .setName("non-prefixed-span")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter().recycle(firstSpan, resource, instrumentationScope);

        Span secondSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(3, 4))
                .setSpanId(AdapterUtils.spanId(2))
                .setName("non-prefixed-span")
                .build();

        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter().recycle(secondSpan, resource, instrumentationScope);

        ProtoTrace protoTraceAdapter = new ProtoTrace(protoSpanAdapter1, true);
        protoTraceAdapter.add(protoSpanAdapter2);

//        template.sendBody("direct:start",protoTraceAdapter);
//
//        MockEndpoint mock = getMockEndpoint("mock:first-output");
//
//        mock.expectedMessageCount(0);
//
//        mock = getMockEndpoint("mock:second-output");
//
//        mock.expectedMessageCount(0);

    }

}
