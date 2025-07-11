package com.siglet.integrationtests.spanlet;

import com.siglet.api.ProcessorContext;
import com.siglet.api.Result;
import com.siglet.api.ResultFactory;
import com.siglet.api.modifiable.trace.ModifiableSpan;
import com.siglet.api.modifiable.trace.ModifiableSpanlet;
import com.siglet.container.Siglet;
import com.siglet.container.adapter.AdapterUtils;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.config.siglet.SigletConfig;
import com.siglet.container.engine.exporter.debug.DebugExporters;
import com.siglet.container.engine.receiver.debug.DebugReceivers;
import com.siglet.parser.Describable;
import com.siglet.parser.NodeChecker;
import com.siglet.parser.NodeCheckerFactory;
import com.siglet.parser.NodeValueBuilder;
import com.siglet.parser.located.Location;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.siglet.parser.schema.SchemaFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SpanletTest {

    @Test
    void simple() {


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
                    type: prefix
                    config:
                      prefix: prefix-value-
                """;

        SigletConfig sigletConfig = new SigletConfig(
                "prefix",
                Location.of(1, 1),
                "adds prefix to span name",
                Location.of(1, 1),
                PrefixSpanProcessor.class,
                Location.of(1, 1),
                new PrefixConfigNodeCheckerFactory().create(),
                Location.of(1, 1), List.of());

        Siglet siglet = new Siglet(config, List.of(sigletConfig));

        siglet.start();

        Span firstSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(0,1))
                .setSpanId(AdapterUtils.spanId(1))
                .setName("span-name")
                .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter firstSpanAdapter = new ProtoSpanAdapter().recycle(firstSpan, resource, instrumentationScope);
        DebugReceivers.INSTANCE.get("receiver").send(firstSpanAdapter);

        siglet.stop();

        List<ProtoSpanAdapter> exporter = DebugExporters.INSTANCE.get("exporter", ProtoSpanAdapter.class);
        assertEquals(1, exporter.size());
        assertEquals("prefix-value-span-name", exporter.getFirst().getName());
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

    public static class PrefixSpanProcessor implements ModifiableSpanlet<PrefixConfig> {

        @Override
        public Result span(ModifiableSpan modifiableSpan, ProcessorContext<PrefixConfig> prefixConfig,
                           ResultFactory resultFactory) {
            modifiableSpan.setName(prefixConfig.getConfig().prefix() + modifiableSpan.getName());
            return resultFactory.proceed();
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
