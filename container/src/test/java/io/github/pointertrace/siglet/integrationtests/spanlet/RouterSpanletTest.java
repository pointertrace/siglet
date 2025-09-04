package io.github.pointertrace.siglet.integrationtests.spanlet;

import io.github.pointertrace.siglet.container.Siglet;
import io.github.pointertrace.siglet.container.adapter.AdapterUtils;
import io.github.pointertrace.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.container.engine.exporter.debug.DebugExporters;
import io.github.pointertrace.siglet.container.engine.receiver.debug.DebugReceivers;
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
                - debug: receiver
                exporters:
                - debug: first-exporter
                - debug: second-exporter
                - debug: third-exporter
                pipelines:
                - name: pipeline
                  from: receiver
                  start: spanlet
                  processors:
                  - spanlet-groovy-router: spanlet
                    to:
                    - first-exporter
                    - second-exporter
                    - third-exporter
                    config:
                      default: third-exporter
                      routes:
                      - when: signal.name == "first"
                        to: first-exporter
                      - when: signal.name == "second"
                        to: second-exporter
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
        DebugReceivers.INSTANCE.get("receiver").send(firstSpanAdapter);

        Span secondSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(0, 1))
                .setSpanId(AdapterUtils.spanId(2))
                .setName("second")
                .build();
        ProtoSpanAdapter secondSpanAdapter = new ProtoSpanAdapter().recycle(secondSpan, resource, instrumentationScope);
        DebugReceivers.INSTANCE.get("receiver").send(secondSpanAdapter);

        Span thirdSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(0, 1))
                .setSpanId(AdapterUtils.spanId(3))
                .setName("third")
                .build();
        ProtoSpanAdapter thirdSpanAdapter = new ProtoSpanAdapter().recycle(thirdSpan, resource, instrumentationScope);
        DebugReceivers.INSTANCE.get("receiver").send(thirdSpanAdapter);

        siglet.stop();


        List<ProtoSpanAdapter> firstExporter = DebugExporters.INSTANCE.get("first-exporter", ProtoSpanAdapter.class);
        assertEquals(1, firstExporter.size());
        assertEquals("first", firstExporter.getFirst().getName());

        List<ProtoSpanAdapter> secondExporter = DebugExporters.INSTANCE.get("second-exporter", ProtoSpanAdapter.class);
        assertEquals(1, secondExporter.size());
        assertEquals("second", secondExporter.getFirst().getName());

        List<ProtoSpanAdapter> thirdExporter = DebugExporters.INSTANCE.get("third-exporter", ProtoSpanAdapter.class);
        assertEquals(1, thirdExporter.size());
        assertEquals("third", thirdExporter.getFirst().getName());
    }

}
