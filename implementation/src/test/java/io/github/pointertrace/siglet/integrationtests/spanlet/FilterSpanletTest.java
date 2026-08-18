package io.github.pointertrace.siglet.integrationtests.spanlet;

import io.github.pointertrace.siglet.impl.Siglet;
import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.github.pointertrace.siglet.impl.adapter.trace.SpanAdapter;
import io.github.pointertrace.siglet.impl.engine.exporter.debug.DebugExporters;
import io.github.pointertrace.siglet.impl.engine.receiver.debug.DebugReceivers;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilterSpanletTest {

    @Test
    void test() {


        String config = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: pipeline
                  from: receiver
                  start: spanlet
                  processors:
                  - spanlet-groovy-filter: spanlet
                    to: exporter
                    config:
                      expression: |
                        signal.name.startsWith("prefix")
                """;

        Siglet siglet = new Siglet(config);

        siglet.start();


        Span firstSpan =
                Span.newBuilder()
                        .setName("prefix-span-name")
                        .setTraceId(AdapterUtils.traceId(0,1))
                        .setSpanId(AdapterUtils.spanId(1))
                        .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        SpanAdapter firstSpanAdapter = new SpanAdapter(firstSpan, resource, instrumentationScope);
        DebugReceivers.INSTANCE.get("receiver").receive(firstSpanAdapter);

        Span secondSpan = Span.newBuilder()
                .setName("span-name")
                .setTraceId(AdapterUtils.traceId(0,1))
                .setSpanId(AdapterUtils.spanId(2))
                .build();
        SpanAdapter secondSpanAdapter = new SpanAdapter(secondSpan, resource, instrumentationScope);
        DebugReceivers.INSTANCE.get("receiver").receive(secondSpanAdapter);

        siglet.stop();


        List<SpanAdapter> exporterDescriptor = DebugExporters.INSTANCE.get("exporter", SpanAdapter.class);
        assertEquals(1, exporterDescriptor.size());
        assertEquals(firstSpanAdapter, exporterDescriptor.getFirst());
    }

    @Test
    void testMultipleExporters() throws InterruptedException {


        String config = """
                receivers:
                - debug: receiver
                exporters:
                - debug: first-exporter
                - debug: second-exporter
                pipelines:
                - name: pipeline
                  from: receiver
                  start: spanlet
                  processors:
                  - spanlet-groovy-filter: spanlet
                    thread-pool-size: 2
                    to:
                    - first-exporter
                    - second-exporter
                    config:
                      expression: |
                        println "sinal.name: ${signal.name}, result=${signal.name.startsWith('prefix')}"
                        signal.name.startsWith("prefix")
                """;

        Siglet siglet = new Siglet(config);

        siglet.start();


        Span firstSpan = Span.newBuilder().setName("prefix-span-name").build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        SpanAdapter firstSpanAdapter = new SpanAdapter(firstSpan, resource, instrumentationScope);
        DebugReceivers.INSTANCE.get("receiver").receive(firstSpanAdapter);

        Span secondSpan = Span.newBuilder().setName("span-name").build();
        SpanAdapter secondSpanAdapter = new SpanAdapter(secondSpan, resource, instrumentationScope);
        DebugReceivers.INSTANCE.get("receiver").receive(secondSpanAdapter);

        siglet.stop();

        // first exporterDescriptor
        List<SpanAdapter> firstExporter = DebugExporters.INSTANCE.get("first-exporter", SpanAdapter.class);
        assertEquals(1, firstExporter.size());
        assertEquals("prefix-span-name", firstExporter.getFirst().getName());


        // second exporterDescriptor
        List<SpanAdapter> secondExporter = DebugExporters.INSTANCE.get("second-exporter", SpanAdapter.class);
        assertEquals(1, secondExporter.size());
        assertEquals("prefix-span-name", secondExporter.getFirst().getName());
    }
}
