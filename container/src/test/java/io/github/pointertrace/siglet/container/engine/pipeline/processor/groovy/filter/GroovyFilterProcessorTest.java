package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.filter;

import io.github.pointertrace.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.container.eventloop.MapSignalDestination;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GroovyFilterProcessorTest {

    @Test
    void process_filterProceed() {

        String script = """
                signal.name == "span-name"
                """;

        SpanletGroovyFilterProcessor groovyFilterProcessorEventLoop = new SpanletGroovyFilterProcessor("groovy-filter",
                script, 1, 1);

        MapSignalDestination finalDestination = new MapSignalDestination("final");

        groovyFilterProcessorEventLoop.connect(finalDestination);

        groovyFilterProcessorEventLoop.start();

        Span span = Span.newBuilder().setName("span-name").build();

        ProtoSpanAdapter protoSpanAdapter = new ProtoSpanAdapter().recycle(span, null, null);

        assertTrue(groovyFilterProcessorEventLoop.send(protoSpanAdapter));

        groovyFilterProcessorEventLoop.stop();

        assertEquals(1, finalDestination.getSize());
        assertTrue(finalDestination.has("Span(traceId:00000000000000000000000000000000,spanId:0000000000000000)"));

    }

    @Test
    void process_filterDrop() {

        String script = """
                signal.name == "invalid name"
                """;

        SpanletGroovyFilterProcessor groovyFilterProcessorEventLoop = new SpanletGroovyFilterProcessor("groovy-filter",
                script, 1, 1);

        MapSignalDestination finalDestination = new MapSignalDestination("final");

        groovyFilterProcessorEventLoop.connect(finalDestination);

        groovyFilterProcessorEventLoop.start();

        Span span = Span.newBuilder().setName("span-name").build();

        ProtoSpanAdapter protoSpanAdapter = new ProtoSpanAdapter().recycle(span, null, null);

        assertTrue(groovyFilterProcessorEventLoop.send(protoSpanAdapter));

        groovyFilterProcessorEventLoop.stop();

        assertEquals(0, finalDestination.getSize());

    }

}