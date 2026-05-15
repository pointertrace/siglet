package io.github.pointertrace.siglet.integrationtests.spanlet;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.impl.Siglet;
import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.github.pointertrace.siglet.impl.adapter.trace.SpanAdapter;
import io.github.pointertrace.siglet.impl.config.siglet.SigletBundle;
import io.github.pointertrace.siglet.impl.config.siglet.fatjar.FatJarBundleLoader;
import io.github.pointertrace.siglet.impl.engine.exporter.debug.DebugExporters;
import io.github.pointertrace.siglet.impl.engine.receiver.debug.DebugReceivers;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpanletDestinationMappingTest {

    @Test
    void simple() {


        String yaml = """
                receivers:
                - debug: receiver
                exporters:
                - debug: first
                - debug: second
                pipelines:
                - name: pipeline
                  from: receiver
                  start: spanlet
                  processors:
                  - split: spanlet
                    to:
                      - a:first
                      - b:second
                    config:
                      prefix: prefix-value-
                """;

        String sigletYaml = """
                siglets:
                - name: split
                  siglet-class: io.github.pointertrace.siglet.integrationtests.spanlet.SpanletDestinationMappingTest$SplitSpanProcessor
                  destinations:
                  - a
                  - b
                  description: adds a prefix to span name
                """;



        SigletBundle sigletBundle = FatJarBundleLoader.loadFromYaml(sigletYaml, SpanletConfigParserFactoryTest.class.getClassLoader(),
                "bundle-mock", () -> {});

        Siglet siglet = new Siglet(yaml, List.of(sigletBundle));

        siglet.start();

        io.opentelemetry.proto.trace.v1.Span spanA = io.opentelemetry.proto.trace.v1.Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(0,1))
                .setSpanId(AdapterUtils.spanId(1))
                .setName("span-a")
                .build();

        io.opentelemetry.proto.trace.v1.Span spanB = io.opentelemetry.proto.trace.v1.Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(0,1))
                .setSpanId(AdapterUtils.spanId(1))
                .setName("span-b")
                .build();

        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        SpanAdapter firstSpanAdapter = new SpanAdapter(spanA, resource, instrumentationScope);
        DebugReceivers.INSTANCE.get("receiver").send(firstSpanAdapter);

        SpanAdapter secondSpanAdapter = new SpanAdapter(spanB, resource, instrumentationScope);
        DebugReceivers.INSTANCE.get("receiver").send(secondSpanAdapter);

        siglet.stop();

        List<SpanAdapter> first = DebugExporters.INSTANCE.get("first", SpanAdapter.class);
        assertEquals(1, first.size());
        assertEquals("span-a", first.getFirst().getName());

        List<SpanAdapter> second = DebugExporters.INSTANCE.get("second", SpanAdapter.class);
        assertEquals(1, second.size());
        assertEquals("span-b", second.getFirst().getName());
    }



    public static class SplitSpanProcessor implements Spanlet<Void> {

        @Override
        public Result span(Span span, Context<Void> prefixConfig,
                           ResultFactory resultFactory) {
            if (span.getName().endsWith("a")) {
                return resultFactory.proceed("a");
            } else {
                return resultFactory.proceed("b");
            }
        }

    }

}
