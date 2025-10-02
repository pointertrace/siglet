package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.router;

import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.github.pointertrace.siglet.impl.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.impl.eventloop.MapSignalDestination;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GroovyRouterSigletTest {

    @Test
    void process() {

        List<Route> routes = List.of(
                new Route("signal.name == 'first-span-name'", "first"),
                new Route("signal.name == 'second-span-name'", "second")
        );

        SpanletGroovyRouterProcessor groovyFilterEventLoop = new SpanletGroovyRouterProcessor("groovy-router",
                "default", routes, 3, 1);

        MapSignalDestination firstDestination = new MapSignalDestination("first");
        MapSignalDestination secondDestination = new MapSignalDestination("second");
        MapSignalDestination defaultDestination = new MapSignalDestination("default");

        groovyFilterEventLoop.connect(firstDestination);
        groovyFilterEventLoop.connect(secondDestination);
        groovyFilterEventLoop.connect(defaultDestination);

        groovyFilterEventLoop.start();

        Span firstSpan = Span.newBuilder()
                .setName("first-span-name")
                .setSpanId(AdapterUtils.spanId(1))
                .build();

        Span secondSpan = Span.newBuilder()
                .setName("second-span-name")
                .setSpanId(AdapterUtils.spanId(2))
                .build();

        Span defaultSpan = Span.newBuilder()
                .setName("default-span-name")
                .setSpanId(AdapterUtils.spanId(3))
                .build();

        ProtoSpanAdapter firstProtoSpanAdapter = new ProtoSpanAdapter().recycle(firstSpan, null, null);
        ProtoSpanAdapter secondProtoSpanAdapter = new ProtoSpanAdapter().recycle(secondSpan, null, null);
        ProtoSpanAdapter defaultProtoSpanAdapter = new ProtoSpanAdapter().recycle(defaultSpan, null, null);

        assertTrue(groovyFilterEventLoop.send(firstProtoSpanAdapter));
        assertTrue(groovyFilterEventLoop.send(secondProtoSpanAdapter));
        assertTrue(groovyFilterEventLoop.send(defaultProtoSpanAdapter));

        groovyFilterEventLoop.stop();

        assertEquals(1, firstDestination.getSize());
        assertTrue(firstDestination.has("Span(traceId:00000000000000000000000000000000,spanId:0000000000000001)"));

        assertEquals(1, secondDestination.getSize());
        assertTrue(secondDestination.has("Span(traceId:00000000000000000000000000000000,spanId:0000000000000002)"));

        assertEquals(1, defaultDestination.getSize());
        assertTrue(defaultDestination.has("Span(traceId:00000000000000000000000000000000,spanId:0000000000000003)"));
    }

}