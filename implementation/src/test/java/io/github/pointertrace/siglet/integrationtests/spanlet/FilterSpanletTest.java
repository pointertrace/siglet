package io.github.pointertrace.siglet.integrationtests.spanlet;

import io.github.pointertrace.siglet.impl.Siglet;
import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.github.pointertrace.siglet.impl.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.impl.engine.exporter.debug.DebugExporters;
import io.github.pointertrace.siglet.impl.engine.receiver.debug.DebugReceivers;
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
                - debug: receiverDescriptor
                exporters:
                - debug: exporterDescriptor
                pipelineDescriptors:
                - name: pipelineDescriptor
                  from: receiverDescriptor
                  start: spanlet
                  processorDescriptors:
                  - spanlet-groovy-filter: spanlet
                    to: exporterDescriptor
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
        ProtoSpanAdapter firstSpanAdapter = new ProtoSpanAdapter().recycle(firstSpan, resource, instrumentationScope);
        assertTrue(DebugReceivers.INSTANCE.get("receiverDescriptor").send(firstSpanAdapter));

        Span secondSpan = Span.newBuilder()
                .setName("span-name")
                .setTraceId(AdapterUtils.traceId(0,1))
                .setSpanId(AdapterUtils.spanId(2))
                .build();
        ProtoSpanAdapter secondSpanAdapter = new ProtoSpanAdapter().recycle(secondSpan, resource, instrumentationScope);
        assertTrue(DebugReceivers.INSTANCE.get("receiverDescriptor").send(secondSpanAdapter));

        siglet.stop();


        List<ProtoSpanAdapter> exporterDescriptor = DebugExporters.INSTANCE.get("exporterDescriptor", ProtoSpanAdapter.class);
        assertEquals(1, exporterDescriptor.size());
        assertEquals(firstSpanAdapter, exporterDescriptor.getFirst());
    }

    @Test
    void testMultipleExporters() {


        String config = """
                receivers:
                - debug: receiverDescriptor
                exporters:
                - debug: first-exporterDescriptor
                - debug: second-exporterDescriptor
                pipelineDescriptors:
                - name: pipelineDescriptor
                  from: receiverDescriptor
                  start: spanlet
                  processorDescriptors:
                  - spanlet-groovy-filter: spanlet
                    to:
                    - first-exporterDescriptor
                    - second-exporterDescriptor
                    config:
                      expression: |
                        signal.name.startsWith("prefix")
                """;

        Siglet siglet = new Siglet(config);

        siglet.start();



        Span firstSpan = Span.newBuilder().setName("prefix-span-name").build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter firstSpanAdapter = new ProtoSpanAdapter().recycle(firstSpan, resource, instrumentationScope);
        DebugReceivers.INSTANCE.get("receiverDescriptor").send(firstSpanAdapter);

        Span secondSpan = Span.newBuilder().setName("span-name").build();
        ProtoSpanAdapter secondSpanAdapter = new ProtoSpanAdapter().recycle(secondSpan, resource, instrumentationScope);
        DebugReceivers.INSTANCE.get("receiverDescriptor").send(secondSpanAdapter);

        siglet.stop();

        // first exporterDescriptor
        List<ProtoSpanAdapter> firstExporter = DebugExporters.INSTANCE.get("first-exporterDescriptor", ProtoSpanAdapter.class);
        assertEquals(1, firstExporter.size());
        assertEquals("prefix-span-name", firstExporter.getFirst().getName());


        // second exporterDescriptor
        List<ProtoSpanAdapter> secondExporter = DebugExporters.INSTANCE.get("first-exporterDescriptor", ProtoSpanAdapter.class);
        assertEquals(1, secondExporter.size());
        assertEquals("prefix-span-name", secondExporter.getFirst().getName());
    }
}
