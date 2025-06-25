package com.siglet.container.engine.pipeline.processor.groovy.filter;

import com.siglet.api.Signal;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.eventloop.MapSignalDestination;
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

        GroovyFilterProcessor groovyFilterProcessorEventLoop = new GroovyFilterProcessor("groovy-filter", script, 1, 1);

        MapSignalDestination finalDestination = new MapSignalDestination("final", Signal.class);

        groovyFilterProcessorEventLoop.connect(finalDestination);

        groovyFilterProcessorEventLoop.start();

        Span span = Span.newBuilder().setName("span-name").build();

        ProtoSpanAdapter protoSpanAdapter = new ProtoSpanAdapter(span, null, null, true);

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

        GroovyFilterProcessor groovyFilterProcessorEventLoop = new GroovyFilterProcessor("groovy-filter", script, 1, 1);

        MapSignalDestination finalDestination = new MapSignalDestination("final", Signal.class);

        groovyFilterProcessorEventLoop.connect(finalDestination);

        groovyFilterProcessorEventLoop.start();

        Span span = Span.newBuilder().setName("span-name").build();

        ProtoSpanAdapter protoSpanAdapter = new ProtoSpanAdapter(span, null, null, true);

        assertTrue(groovyFilterProcessorEventLoop.send(protoSpanAdapter));

        groovyFilterProcessorEventLoop.stop();

        assertEquals(0, finalDestination.getSize());

    }

}