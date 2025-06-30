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

class ActionSpanletTest {

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
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;


        Siglet siglet = new Siglet(config);

        siglet.start();


        Span span = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(0, 1))
                .setSpanId(AdapterUtils.spanId(1))
                .setName("span-name")
                .build();

        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter = new ProtoSpanAdapter().recycle(span, resource, instrumentationScope);

        assertTrue(DebugReceivers.INSTANCE.get("receiver").send(protoSpanAdapter));

        siglet.stop();

        List<ProtoSpanAdapter> signals = DebugExporters.INSTANCE.get("exporter", ProtoSpanAdapter.class);
        assertEquals(1, signals.size());
        assertEquals("span-name-suffix", signals.getFirst().getName());

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
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        Siglet siglet = new Siglet(config);

        siglet.start();


        Span span = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(0, 1))
                .setSpanId(AdapterUtils.spanId(1))
                .setName("span-name").build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter = new ProtoSpanAdapter().recycle(span, resource, instrumentationScope);
        assertTrue(DebugReceivers.INSTANCE.get("receiver").send(protoSpanAdapter));

        siglet.stop();

        // first output
        List<ProtoSpanAdapter> firstExporter = DebugExporters.INSTANCE.get("first-exporter", ProtoSpanAdapter.class);
        assertEquals(1, firstExporter.size());
        assertEquals("span-name-suffix", firstExporter.getFirst().getName());


        // second output
        List<ProtoSpanAdapter> secondExporter = DebugExporters.INSTANCE.get("first-exporter", ProtoSpanAdapter.class);
        assertEquals(1, secondExporter.size());
        assertEquals("span-name-suffix", secondExporter.getFirst().getName());

    }
}
