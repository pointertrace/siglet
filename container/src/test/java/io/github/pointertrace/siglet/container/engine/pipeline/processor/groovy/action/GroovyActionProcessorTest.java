package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.action;

import io.github.pointertrace.siglet.container.adapter.AdapterUtils;
import io.github.pointertrace.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.container.eventloop.MapSignalDestination;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GroovyActionProcessorTest {

    @Test
    void process() {

        String script = """
                signal.name = "prefix-" + signal.name
                context.attributes["name-with-prefix"] = signal.name
                """;

        SpanletGroovyActionProcessor groovyActionProcessorEventLoop = new SpanletGroovyActionProcessor("groovy-action",
                script, 1, 1);

        MapSignalDestination finalDestination = new MapSignalDestination("final");

        groovyActionProcessorEventLoop.connect(finalDestination);

        groovyActionProcessorEventLoop.start();

        Span span =
                Span.newBuilder()
                        .setTraceId(AdapterUtils.traceId(0, 1))
                        .setSpanId(AdapterUtils.spanId(1))
                        .setName("span-name").build();

        ProtoSpanAdapter protoSpanAdapter = new ProtoSpanAdapter().recycle(span, null, null);


        assertTrue(groovyActionProcessorEventLoop.send(protoSpanAdapter));

        groovyActionProcessorEventLoop.stop();

        assertEquals(1, finalDestination.getSize());
        assertTrue(finalDestination.has("Span(traceId:00000000000000000000000000000001,spanId:0000000000000001)"));
        assertEquals(protoSpanAdapter, finalDestination.get("Span(traceId:00000000000000000000000000000001," +
                "spanId:0000000000000001)", ProtoSpanAdapter.class));
        assertEquals("prefix-span-name", protoSpanAdapter.getName());
        assertEquals("prefix-span-name",
                groovyActionProcessorEventLoop.getContext().getAttributes().get("name-with-prefix"));

    }

    @Test
    void process_drop() {

        String script = """
                signal.name = "prefix-" + signal.name
                context.attributes["name-with-prefix"] = signal.name
                drop()
                """;

        SpanletGroovyActionProcessor groovyActionProcessorEventLoop = new SpanletGroovyActionProcessor("groovy-action",
                script, 1, 1);

        MapSignalDestination finalDestination = new MapSignalDestination("final");

        groovyActionProcessorEventLoop.connect(finalDestination);

        groovyActionProcessorEventLoop.start();

        Span span = Span.newBuilder().setName("span-name").build();
        ProtoSpanAdapter protoSpanAdapter = new ProtoSpanAdapter().recycle(span, null, null);


        assertTrue(groovyActionProcessorEventLoop.send(protoSpanAdapter));

        groovyActionProcessorEventLoop.stop();

        assertEquals("prefix-span-name", protoSpanAdapter.getName());
        assertEquals("prefix-span-name", groovyActionProcessorEventLoop.getContext().getAttributes()
                        .get("name-with-prefix"));

        assertEquals(0, finalDestination.getSize());
    }

    @Test
    void process_proceedDestination() {

        String script = """
                signal.name = "prefix-" + signal.name
                context.attributes["name-with-prefix"] = signal.name
                proceed("other")
                """;

        SpanletGroovyActionProcessor groovyActionProcessorEventLoop = new SpanletGroovyActionProcessor("groovy-action",
                 script, 1, 1);

        MapSignalDestination finalDestination = new MapSignalDestination("final");
        MapSignalDestination otherDestination = new MapSignalDestination("other");


        groovyActionProcessorEventLoop.connect(finalDestination);
        groovyActionProcessorEventLoop.connect(otherDestination);

        groovyActionProcessorEventLoop.start();

        Span span = Span.newBuilder().setName("span-name").build();
        ProtoSpanAdapter protoSpanAdapter = new ProtoSpanAdapter().recycle(span, null, null);


        assertTrue(groovyActionProcessorEventLoop.send(protoSpanAdapter));

        groovyActionProcessorEventLoop.stop();

        assertEquals("prefix-span-name", protoSpanAdapter.getName());
        assertEquals("prefix-span-name", groovyActionProcessorEventLoop.getContext().getAttributes()
                .get("name-with-prefix"));

        assertEquals(0, finalDestination.getSize());
        assertEquals(1, otherDestination.getSize());
        assertTrue(otherDestination.has("Span(traceId:00000000000000000000000000000000,spanId:0000000000000000)"));
        assertEquals(protoSpanAdapter, otherDestination.get("Span(traceId:00000000000000000000000000000000," +
                "spanId:0000000000000000)", ProtoSpanAdapter.class));

    }
}