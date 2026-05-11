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

class RouterSpanletTest {

    @Test
    void test() {


        String config = """
                receivers:
                - debug: receiverDescriptor
                exporters:
                - debug: first-exporterDescriptor
                - debug: second-exporterDescriptor
                - debug: third-exporterDescriptor
                pipelineDescriptors:
                - name: pipelineDescriptor
                  from: receiverDescriptor
                  start: spanlet
                  processorDescriptors:
                  - spanlet-groovy-router: spanlet
                    to:
                    - first-exporterDescriptor
                    - second-exporterDescriptor
                    - third-exporterDescriptor
                    config:
                      default: third-exporterDescriptor
                      routes:
                      - when: signal.name == "first"
                        to: first-exporterDescriptor
                      - when: signal.name == "second"
                        to: second-exporterDescriptor
                """;

        Siglet siglet = new Siglet(config);

        siglet.start();

        Span firstSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(0, 1))
                .setSpanId(AdapterUtils.spanId(1))
                .setName("first")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter firstSpanAdapter = new ProtoSpanAdapter().recycle(firstSpan, resource, instrumentationScope);
        DebugReceivers.INSTANCE.get("receiverDescriptor").send(firstSpanAdapter);

        Span secondSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(0, 1))
                .setSpanId(AdapterUtils.spanId(2))
                .setName("second")
                .build();
        ProtoSpanAdapter secondSpanAdapter = new ProtoSpanAdapter().recycle(secondSpan, resource, instrumentationScope);
        DebugReceivers.INSTANCE.get("receiverDescriptor").send(secondSpanAdapter);

        Span thirdSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(0, 1))
                .setSpanId(AdapterUtils.spanId(3))
                .setName("third")
                .build();
        ProtoSpanAdapter thirdSpanAdapter = new ProtoSpanAdapter().recycle(thirdSpan, resource, instrumentationScope);
        DebugReceivers.INSTANCE.get("receiverDescriptor").send(thirdSpanAdapter);

        siglet.stop();


        List<ProtoSpanAdapter> firstExporter = DebugExporters.INSTANCE.get("first-exporterDescriptor", ProtoSpanAdapter.class);
        assertEquals(1, firstExporter.size());
        assertEquals("first", firstExporter.getFirst().getName());

        List<ProtoSpanAdapter> secondExporter = DebugExporters.INSTANCE.get("second-exporterDescriptor", ProtoSpanAdapter.class);
        assertEquals(1, secondExporter.size());
        assertEquals("second", secondExporter.getFirst().getName());

        List<ProtoSpanAdapter> thirdExporter = DebugExporters.INSTANCE.get("third-exporterDescriptor", ProtoSpanAdapter.class);
        assertEquals(1, thirdExporter.size());
        assertEquals("third", thirdExporter.getFirst().getName());
    }

}
