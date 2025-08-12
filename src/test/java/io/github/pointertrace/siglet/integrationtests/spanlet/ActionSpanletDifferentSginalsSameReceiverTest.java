package io.github.pointertrace.siglet.integrationtests.spanlet;

import io.github.pointertrace.siglet.container.Siglet;
import io.github.pointertrace.siglet.container.adapter.AdapterUtils;
import io.github.pointertrace.siglet.container.adapter.metric.ProtoMetricAdapter;
import io.github.pointertrace.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.container.engine.exporter.debug.DebugExporters;
import io.github.pointertrace.siglet.container.engine.receiver.debug.DebugReceivers;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionSpanletDifferentSginalsSameReceiverTest {

    @Test
    void test() {


        String config = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: span-pipeline
                  from: receiver
                  start: spanlet
                  processors:
                  - name: spanlet
                    kind: spanlet
                    to: exporter
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-span"
                - name: metric-pipeline
                  from: receiver
                  start: metriclet
                  processors:
                  - name: metriclet
                    kind: metriclet
                    to: exporter
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-metric"
                """;


        Siglet siglet = new Siglet(config);

        siglet.start();


        Span span = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(0, 1))
                .setSpanId(AdapterUtils.spanId(1))
                .setName("name")
                .build();

        Metric metric = Metric.newBuilder()
                .setName("name")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter = new ProtoSpanAdapter().recycle(span, resource, instrumentationScope);
        ProtoMetricAdapter protoMetricAdapter = new ProtoMetricAdapter().recycle(metric, resource, instrumentationScope);

        assertTrue(DebugReceivers.INSTANCE.get("receiver").send(protoSpanAdapter));
        assertTrue(DebugReceivers.INSTANCE.get("receiver").send(protoMetricAdapter));

        siglet.stop();

        assertEquals(2, DebugExporters.INSTANCE.get("exporter").size());

        List<ProtoSpanAdapter> SpanSignals = DebugExporters.INSTANCE.get("exporter", ProtoSpanAdapter.class);

        assertEquals(1, SpanSignals.size());
        assertEquals("name-span", SpanSignals.getFirst().getName());

        List<ProtoMetricAdapter> metricSignals = DebugExporters.INSTANCE.get("exporter", ProtoMetricAdapter.class);

        assertEquals(1, metricSignals.size());
        assertEquals("name-metric", metricSignals.getFirst().getName());
    }


}
