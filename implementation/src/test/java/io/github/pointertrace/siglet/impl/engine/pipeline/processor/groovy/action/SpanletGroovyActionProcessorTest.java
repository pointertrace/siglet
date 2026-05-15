package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.action;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.signal.metric.Metric;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.github.pointertrace.siglet.impl.adapter.trace.SpanAdapter;
import io.github.pointertrace.siglet.impl.config.descriptor.ProcessorDescriptor;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.Component;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.eventloop.MockSignalDestination;
import io.github.pointertrace.siglet.parser.*;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpanletGroovyActionProcessorTest {

    private MockSignalDestination defaultDestination;

    private MockSignalDestination otherDestination;

    private SpanAdapter spanAdapter;

    @BeforeEach
    public void setUp() {

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

    }

    @Test
    void process() {

        String script = """
                  signal.name = "prefix-" + signal.name
                  context.attributes["new-name"] = signal.name
                """;


        GroovyActionProcessor groovyActionProcessor = new GroovyActionProcessor("action", script, SignalCapabilities.of(Span.class), 1, 1);

        groovyActionProcessor.connect(defaultDestination);

        groovyActionProcessor.start();

        groovyActionProcessor.send(spanAdapter);

        groovyActionProcessor.stop();

        assertEquals(1, defaultDestination.getSize());

        Span processedSpan = defaultDestination.get(0, Span.class);

        assertEquals("prefix-span-name", processedSpan.getName());
        assertTrue(groovyActionProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name", groovyActionProcessor.getContext().getAttributes().get("new-name"));
    }

    @Test
    void process_drop() {

        String script = """
                  signal.name = "prefix-" + signal.name
                  context.attributes["new-name"] = signal.name
                  drop()
                """;

        GroovyActionProcessor groovyActionProcessor = new GroovyActionProcessor("action", script, SignalCapabilities.of(Span.class), 1, 1);

        groovyActionProcessor.connect(defaultDestination);

        groovyActionProcessor.start();

        groovyActionProcessor.send(spanAdapter);

        groovyActionProcessor.stop();

        assertEquals(0, defaultDestination.getSize());

        assertEquals("prefix-span-name", spanAdapter.getName());
        assertTrue(groovyActionProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name", groovyActionProcessor.getContext().getAttributes().get("new-name"));
    }

    @Test
    void process_proceedToDestination() {

        String script = """
                  signal.name = "prefix-" + signal.name
                  context.attributes["new-name"] = signal.name
                  proceed("other")
                """;

        GroovyActionProcessor groovyActionProcessor = new GroovyActionProcessor("action", script, SignalCapabilities.of(Span.class), 1, 1);

        groovyActionProcessor.connect(defaultDestination);
        groovyActionProcessor.connect(otherDestination);

        groovyActionProcessor.start();

        groovyActionProcessor.send(spanAdapter);

        groovyActionProcessor.stop();

        assertEquals(0, defaultDestination.getSize());
        assertEquals(1, otherDestination.getSize());

        Span processedSpan = otherDestination.get(0, Span.class);

        assertEquals("prefix-span-name", processedSpan.getName());
        assertTrue(groovyActionProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name", groovyActionProcessor.getContext().getAttributes().get("new-name"));

    }

    @Test
    void checkCompatibility() {

        String script = """
                  signal.name = "prefix-" + signal.name
                  context.attributes["new-name"] = signal.name
                  proceed("other")
                """;

        GroovyActionProcessor groovyActionProcessor = new GroovyActionProcessor("action", script, SignalCapabilities.of(Span.class), 1, 1);

        SigletError ex = assertThrows(SigletError.class, () ->
                groovyActionProcessor.connect(new MockSignalDestination("mock", SignalCapabilities.of(Metric.class))));

        assertEquals("Cannot connect processor [action] to [mock] because they have incompatible signal " +
                        "capabilities. Processor generates [io.github.pointertrace.siglet.api.signal.trace.Span] and " +
                        "destination expects [io.github.pointertrace.siglet.api.signal.metric.Metric]",
                ex.getMessage());
    }

}