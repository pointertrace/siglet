package io.github.pointertrace.siglet.integrationtests.spanlet;

import io.github.pointertrace.siglet.impl.Siglet;
import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.github.pointertrace.siglet.impl.adapter.trace.ProtoSpanAdapter;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.Test;

class ActionSpanletTest {

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
                  - spanlet-groovy-action: spanlet
                    to: exporterDescriptor
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

//        assertTrue(DebugReceivers.INSTANCE.get("receiverDescriptor").send(protoSpanAdapter));

        siglet.stop();

//        List<ProtoSpanAdapter> signals = DebugExporters.INSTANCE.get("exporterDescriptor", ProtoSpanAdapter.class);
//        assertEquals(1, signals.size());
//        assertEquals("span-name-suffix", signals.getFirst().getName());
//
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
                  - spanlet-groovy-action: spanlet
                    to:
                    - first-exporterDescriptor
                    - second-exporterDescriptor
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
//        assertTrue(DebugReceivers.INSTANCE.get("receiverDescriptor").send(protoSpanAdapter));

        siglet.stop();

        // first output
//        List<ProtoSpanAdapter> firstExporter = DebugExporters.INSTANCE.get("first-exporterDescriptor", ProtoSpanAdapter.class);
//        assertEquals(1, firstExporter.size());
//        assertEquals("span-name-suffix", firstExporter.getFirst().getName());


        // second output
//        List<ProtoSpanAdapter> secondExporter = DebugExporters.INSTANCE.get("first-exporterDescriptor", ProtoSpanAdapter.class);
//        assertEquals(1, secondExporter.size());
//        assertEquals("span-name-suffix", secondExporter.getFirst().getName());

    }
}
