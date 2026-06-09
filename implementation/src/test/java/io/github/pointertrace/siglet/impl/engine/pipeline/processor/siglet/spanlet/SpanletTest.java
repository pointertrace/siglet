package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.spanlet;

import io.github.pointertrace.siglet.api.*;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.github.pointertrace.siglet.impl.adapter.trace.SpanAdapter;
import io.github.pointertrace.siglet.impl.engine.SigletMetrics;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.eventloop.MockSignalDestination;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.string;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SpanletTest {


    private Config config;

    private MockSignalDestination defaultDestination;

    private MockSignalDestination otherDestination;

    private SpanAdapter spanAdapter;

    private SigletMetrics sigletMetrics;

    @BeforeEach
    public void setUp() {

        config = new Config();
        config.setPrefix("prefix-");

        defaultDestination = new MockSignalDestination("default", SignalCapabilities.of(Span.class));

        otherDestination = new MockSignalDestination("other", SignalCapabilities.of(Span.class));

        io.opentelemetry.proto.trace.v1.Span span = io.opentelemetry.proto.trace.v1.Span.newBuilder()
                .setName("span-name")
                .setSpanId(AdapterUtils.spanId(1))
                .setTraceId(AdapterUtils.traceId(0, 1))
                .build();

        Resource resource = Resource.newBuilder().build();

        InstrumentationScope scope = InstrumentationScope.newBuilder().setName("scope").build();

        spanAdapter = new SpanAdapter(span, resource, scope);

        sigletMetrics = new SigletMetrics();

    }


    @Test
    public void process() {

        SpanletProcessor spanletProcessor = new SpanletProcessor("processor", new PrefixSpanlet(), config, 1, 1,
                new SigletMetrics(), Map.of());

        spanletProcessor.connect(defaultDestination);

        spanletProcessor.start();

        spanletProcessor.send(spanAdapter);

        spanletProcessor.stop();

        assertEquals(1, defaultDestination.getSize());

        Span processedSpan = defaultDestination.get(0, Span.class);

        assertEquals("prefix-span-name", processedSpan.getName());
        assertTrue(spanletProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name", spanletProcessor.getContext().getAttributes().get("new-name"));
    }

    @Test
    public void process_drop() {

        SpanletProcessor spanletProcessor = new SpanletProcessor("processor", new PrefixSpanletDrop(), config, 1, 1,sigletMetrics, Map.of());

        spanletProcessor.connect(defaultDestination);

        spanletProcessor.start();

        spanletProcessor.send(spanAdapter);

        spanletProcessor.stop();

        assertEquals(0, defaultDestination.getSize());

        assertTrue(spanletProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name", spanletProcessor.getContext().getAttributes().get("new-name"));
    }

    @Test
    public void process_proceedToDestination() {

        SpanletProcessor spanletProcessor = new SpanletProcessor("processor",
                new PrefixSpanletProceedToDestination(), config, 1, 1,sigletMetrics, Map.of());

        spanletProcessor.connect(defaultDestination);
        spanletProcessor.connect(otherDestination);

        spanletProcessor.start();

        spanletProcessor.send(spanAdapter);

        spanletProcessor.stop();

        assertEquals(0, defaultDestination.getSize());
        assertEquals(1, otherDestination.getSize());

        Span processedSpan = otherDestination.get(0, Span.class);
        assertEquals("prefix-span-name", processedSpan.getName());

        assertTrue(spanletProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name", spanletProcessor.getContext().getAttributes().get("new-name"));
    }

    @Test
    public void process_proceedToDestinationMapping() {

        SpanletProcessor spanletProcessor = new SpanletProcessor("processor",
                new PrefixSpanletProceedToDestinationMapped(), config, 1, 1,sigletMetrics,
                Map.of("other-mapping", "other"));

        spanletProcessor.connect(defaultDestination);
        spanletProcessor.connect(otherDestination);

        spanletProcessor.start();

        spanletProcessor.send(spanAdapter);

        spanletProcessor.stop();

        assertEquals(0, defaultDestination.getSize());
        assertEquals(1, otherDestination.getSize());

        Span processedSpan = otherDestination.get(0, Span.class);
        assertEquals("prefix-span-name", processedSpan.getName());

        assertTrue(spanletProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name", spanletProcessor.getContext().getAttributes().get("new-name"));
    }

    public static class PrefixSpanlet implements Spanlet<Config> {

        @Override
        public Result span(Span span, Context<Config> context, ResultFactory resultFactory) {
            String newName = context.getConfig().getPrefix() + span.getName();
            span.setName(newName);
            context.getAttributes().put("new-name", newName);
            return resultFactory.proceed();
        }
    }

    public static class PrefixSpanletDrop implements Spanlet<Config> {

        @Override
        public Result span(Span span, Context<Config> context, ResultFactory resultFactory) {
            String newName = context.getConfig().getPrefix() + span.getName();
            span.setName(newName);
            context.getAttributes().put("new-name", newName);
            return resultFactory.drop();
        }
    }

    public static class PrefixSpanletProceedToDestination implements Spanlet<Config> {

        @Override
        public Result span(Span span, Context<Config> context, ResultFactory resultFactory) {
            String newName = context.getConfig().getPrefix() + span.getName();
            span.setName(newName);
            context.getAttributes().put("new-name", newName);
            return resultFactory.proceed("other");
        }
    }

    public static class PrefixSpanletProceedToDestinationMapped implements Spanlet<Config> {

        @Override
        public Result span(Span span, Context<Config> context, ResultFactory resultFactory) {
            String newName = context.getConfig().getPrefix() + span.getName();
            span.setName(newName);
            context.getAttributes().put("new-name", newName);
            return resultFactory.proceed("other-mapping");
        }
    }

    public static class Config {

        private String prefix;

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
    }


}