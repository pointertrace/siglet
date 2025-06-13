package com.siglet.integrationtests.spanlet;

import com.siglet.container.Siglet;
import com.siglet.container.adapter.AdapterUtils;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.engine.exporter.debug.DebugExporters;
import com.siglet.container.engine.receiver.debug.DebugReceivers;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                  signal: trace
                  from: receiver
                  start: spanlet
                  processors:
                  - name: spanlet
                    kind: spanlet
                    to: exporter
                    type: groovy-filter
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
        ProtoSpanAdapter firstSpanAdapter = new ProtoSpanAdapter(firstSpan, resource, instrumentationScope, true);
        assertTrue(DebugReceivers.INSTANCE.get("receiver").send(firstSpanAdapter));

        Span secondSpan = Span.newBuilder()
                .setName("span-name")
                .setTraceId(AdapterUtils.traceId(0,1))
                .setSpanId(AdapterUtils.spanId(2))
                .build();
        ProtoSpanAdapter secondSpanAdapter = new ProtoSpanAdapter(secondSpan, resource,
                instrumentationScope,
                true);
        assertTrue(DebugReceivers.INSTANCE.get("receiver").send(secondSpanAdapter));

        siglet.stop();


        List<ProtoSpanAdapter> exporter = DebugExporters.INSTANCE.get("exporter", ProtoSpanAdapter.class);
        assertEquals(1, exporter.size());
        assertEquals(firstSpanAdapter, exporter.getFirst());
    }

    @Test
    void testMultipleExporters() {


        String config = """
                receivers:
                - debug: receiver
                exporters:
                - debug: first-exporter
                - debug: second-exporter
                pipelines:
                - name: pipeline
                  signal: trace
                  from: receiver
                  start: spanlet
                  processors:
                  - name: spanlet
                    kind: spanlet
                    to:
                    - first-exporter
                    - second-exporter
                    type: groovy-filter
                    config:
                      expression: |
                        signal.name.startsWith("prefix")
                """;

        Siglet siglet = new Siglet(config);

        siglet.start();



        Span firstSpan = Span.newBuilder().setName("prefix-span-name").build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter firstSpanAdapter = new ProtoSpanAdapter(firstSpan, resource,
                instrumentationScope,
                true);
        DebugReceivers.INSTANCE.get("receiver").send(firstSpanAdapter);

        Span secondSpan = Span.newBuilder().setName("span-name").build();
        ProtoSpanAdapter secondSpanAdapter = new ProtoSpanAdapter(secondSpan, resource, instrumentationScope, true);
        DebugReceivers.INSTANCE.get("receiver").send(secondSpanAdapter);

        siglet.stop();

        // first exporter
        List<ProtoSpanAdapter> firstExporter = DebugExporters.INSTANCE.get("first-exporter", ProtoSpanAdapter.class);
        assertEquals(1, firstExporter.size());
        assertEquals("prefix-span-name", firstExporter.getFirst().getName());


        // second exporter
        List<ProtoSpanAdapter> secondExporter = DebugExporters.INSTANCE.get("first-exporter", ProtoSpanAdapter.class);
        assertEquals(1, secondExporter.size());
        assertEquals("prefix-span-name", secondExporter.getFirst().getName());
    }
}
