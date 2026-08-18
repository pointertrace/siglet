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

class SpanletNoConfigTest {

    @Test
    void simple() {


        String yaml = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: pipeline
                  from: receiver
                  start: spanlet
                  processors:
                  - prefix-spanlet: spanlet
                    to: exporter
                """;

        String sigletYaml = """
                siglets:
                - name: prefix-spanlet
                  siglet-class: io.github.pointertrace.siglet.integrationtests.spanlet.SpanletNoConfigTest$PrefixSpanProcessor
                  description: adds a prefix to span name
                """;



        SigletBundle sigletBundle = FatJarBundleLoader.loadFromYaml(sigletYaml, SpanletConfigParserFactoryTest.class.getClassLoader(),
                "bundle-mock", () -> {});

        Siglet siglet = new Siglet(yaml, List.of(sigletBundle));

        siglet.start();

        io.opentelemetry.proto.trace.v1.Span firstSpan = io.opentelemetry.proto.trace.v1.Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(0, 1))
                .setSpanId(AdapterUtils.spanId(1))
                .setName("span-name")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        SpanAdapter firstSpanAdapter = new SpanAdapter(firstSpan, resource, instrumentationScope);
        DebugReceivers.INSTANCE.get("receiver").receive(firstSpanAdapter);

        siglet.stop();

        List<SpanAdapter> exporter = DebugExporters.INSTANCE.get("exporter", SpanAdapter.class);
        assertEquals(1, exporter.size());
        assertEquals("prefix-span-name", exporter.getFirst().getName());
    }

    public static class PrefixSpanProcessor implements Spanlet<Void> {

        @Override
        public Result span(Span span, Context<Void> config,
                           ResultFactory resultFactory) {
            span.setName("prefix-" + span.getName());
            return resultFactory.proceed();
        }

    }

}
