package io.github.pointertrace.siglet.integrationtests.spanlet;

import io.github.pointertrace.siget.parser.Describable;
import io.github.pointertrace.siget.parser.NodeChecker;
import io.github.pointertrace.siget.parser.NodeCheckerFactory;
import io.github.pointertrace.siget.parser.NodeValueBuilder;
import io.github.pointertrace.siget.parser.located.Location;
import io.github.pointertrace.siglet.api.ProcessorContext;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.container.Siglet;
import io.github.pointertrace.siglet.container.adapter.AdapterUtils;
import io.github.pointertrace.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.container.config.raw.LocatedString;
import io.github.pointertrace.siglet.container.config.siglet.SigletConfig;
import io.github.pointertrace.siglet.container.engine.exporter.debug.DebugExporters;
import io.github.pointertrace.siglet.container.engine.receiver.debug.DebugReceivers;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.pointertrace.siget.parser.schema.SchemaFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SpanletDestinationMappingTest {

    @Test
    void simple() {


        String config = """
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
                  - name: spanlet
                    kind: spanlet
                    to:
                      - a:first
                      - b:second
                    type: split
                    config:
                      prefix: prefix-value-
                """;

        SigletConfig sigletConfig = new SigletConfig(
                "split",
                Location.of(1, 1),
                "split spans between two destinations",
                Location.of(1, 1),
                SplitSpanProcessor.class,
                Location.of(1, 1),
                new PrefixConfigNodeCheckerFactory().create(),
                Location.of(1, 1),
                List.of(
                        new LocatedString("a",Location.of(1,1)),
                        new LocatedString("b",Location.of(1,1))
                ));

        Siglet siglet = new Siglet(config, List.of(sigletConfig));

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
        ProtoSpanAdapter firstSpanAdapter = new ProtoSpanAdapter().recycle(spanA, resource, instrumentationScope);
        DebugReceivers.INSTANCE.get("receiver").send(firstSpanAdapter);

        ProtoSpanAdapter secondSpanAdapter = new ProtoSpanAdapter().recycle(spanB, resource, instrumentationScope);
        DebugReceivers.INSTANCE.get("receiver").send(secondSpanAdapter);

        siglet.stop();

        List<ProtoSpanAdapter> first = DebugExporters.INSTANCE.get("first", ProtoSpanAdapter.class);
        assertEquals(1, first.size());
        assertEquals("span-a", first.getFirst().getName());

        List<ProtoSpanAdapter> second = DebugExporters.INSTANCE.get("second", ProtoSpanAdapter.class);
        assertEquals(1, second.size());
        assertEquals("span-b", second.getFirst().getName());
    }

    public record PrefixConfig(String prefix) implements Describable {

        @Override
        public String describe(int level) {
            return "here should be the description of prefix config!";
        }
    }


    public static class PrefixConfigBuilder implements NodeValueBuilder {

        private String prefix;

        public PrefixConfigBuilder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public PrefixConfig build() {
            return new PrefixConfig(prefix);
        }

    }

    public static class SplitSpanProcessor implements Spanlet<PrefixConfig> {

        @Override
        public Result span(Span unmodifiableSpan, ProcessorContext<PrefixConfig> prefixConfig,
                           ResultFactory resultFactory) {
            if (unmodifiableSpan.getName().endsWith("a")) {
                return resultFactory.proceed("a");
            } else {
                return resultFactory.proceed("b");
            }
        }

    }

    public static class PrefixConfigNodeCheckerFactory implements NodeCheckerFactory {

        @Override
        public NodeChecker create() {
            return strictObject(PrefixConfigBuilder::new,
                    requiredProperty(PrefixConfigBuilder::prefix, "prefix", text()));
        }
    }

}
