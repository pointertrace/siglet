package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.filter;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.signal.metric.Metric;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.github.pointertrace.siglet.impl.adapter.trace.SpanAdapter;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.eventloop.MockSignalDestination;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SpanletGroovyFilterProcessorTest {

    private MockSignalDestination destination;

    private SpanAdapter spanAdapter;


    @BeforeEach
    public void setUp() {

        destination = new MockSignalDestination("default", SignalCapabilities.of(Span.class));

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
    void process_match() {

        GroovyFilterProcessor groovyFilterProcessor = new GroovyFilterProcessor(
                "filter", "signal.name == 'span-name'", SignalCapabilities.of(Span.class), 1, 1);

        groovyFilterProcessor.connect(destination);

        groovyFilterProcessor.start();

        groovyFilterProcessor.send(spanAdapter);

        groovyFilterProcessor.stop();

        assertEquals(1, destination.getSize());

        Span processedSpan = destination.get(0, Span.class);

        assertEquals("span-name", processedSpan.getName());
    }

    @Test
    void process_nonMatch() {

        GroovyFilterProcessor groovyFilterProcessor = new GroovyFilterProcessor(
                "filter", "signal.name == 'other-span-name'", SignalCapabilities.of(Span.class), 1, 1);

        groovyFilterProcessor.connect(destination);

        groovyFilterProcessor.connect(destination);

        groovyFilterProcessor.start();

        groovyFilterProcessor.send(spanAdapter);

        groovyFilterProcessor.stop();

        assertEquals(0, destination.getSize());
    }

    @Test
    void checkCompatibility() {


        GroovyFilterProcessor groovyFilterProcessor = new GroovyFilterProcessor(
                "filter", "true", SignalCapabilities.of(Span.class), 1, 1);

        SigletError ex = assertThrows(SigletError.class, () ->
                groovyFilterProcessor.connect(new MockSignalDestination("mock", SignalCapabilities.of(Metric.class))));

        assertEquals("Cannot connect processor [filter] to [mock] because they have incompatible signal " +
                        "capabilities. Processor generates [io.github.pointertrace.siglet.api.signal.trace.Span] and " +
                        "destination expects [io.github.pointertrace.siglet.api.signal.metric.Metric]",
                ex.getMessage());
    }


}
