package io.github.pointertrace.siglet.integrationtests.spanlet;

import io.github.pointertrace.siglet.parser.located.Location;
import io.github.pointertrace.siglet.api.ProcessorContext;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.container.Siglet;
import io.github.pointertrace.siglet.container.adapter.AdapterUtils;
import io.github.pointertrace.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.container.config.siglet.parser.SigletConfig;
import io.github.pointertrace.siglet.container.engine.exporter.debug.DebugExporters;
import io.github.pointertrace.siglet.container.engine.receiver.debug.DebugReceivers;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpanletNoConfigTest {

//    @Test
//    void simple() {
//
//
//        String config = """
//                receivers:
//                - debug: receiver
//                exporters:
//                - debug: exporter
//                pipelines:
//                - name: pipeline
//                  from: receiver
//                  start: spanlet
//                  processors:
//                  - prefix: spanlet
//                    to: exporter
//                """;
//
//        SigletConfig sigletConfig = new SigletConfig(
//                "prefix",
//                Location.of(1, 1),
//                "adds prefix to span name",
//                Location.of(1, 1),
//                PrefixSpanProcessor.class,
//                Location.of(1, 1),
//                null,
//                Location.of(1, 1), List.of());
//
//        Siglet siglet = new Siglet(config, List.of(sigletConfig));
//
//        siglet.start();
//
//        io.opentelemetry.proto.trace.v1.Span firstSpan = io.opentelemetry.proto.trace.v1.Span.newBuilder()
//                .setTraceId(AdapterUtils.traceId(0, 1))
//                .setSpanId(AdapterUtils.spanId(1))
//                .setName("span-name")
//                .build();
//        Resource resource = Resource.newBuilder().build();
//        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
//        ProtoSpanAdapter firstSpanAdapter = new ProtoSpanAdapter().recycle(firstSpan, resource, instrumentationScope);
//        DebugReceivers.INSTANCE.get("receiver").send(firstSpanAdapter);
//
//        siglet.stop();
//
//        List<ProtoSpanAdapter> exporter = DebugExporters.INSTANCE.get("exporter", ProtoSpanAdapter.class);
//        assertEquals(1, exporter.size());
//        assertEquals("prefix-span-name", exporter.getFirst().getName());
//    }
//
//    public static class PrefixSpanProcessor implements Spanlet<Void> {
//
//        @Override
//        public Result span(Span span, ProcessorContext<Void> config,
//                           ResultFactory resultFactory) {
//            span.setName("prefix-" + span.getName());
//            return resultFactory.proceed();
//        }
//
//    }

}
