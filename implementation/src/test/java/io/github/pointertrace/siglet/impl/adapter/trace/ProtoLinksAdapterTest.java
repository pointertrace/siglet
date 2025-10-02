package io.github.pointertrace.siglet.impl.adapter.trace;

import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.github.pointertrace.siglet.impl.adapter.common.ProtoAttributesAdapter;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProtoLinksAdapterTest {


    private List<Span.Link> protoLinks;

    private ProtoLinksAdapter protoLinksAdapter;

    @BeforeEach
    void setUp() {
        protoLinks = List.of(
                Span.Link.newBuilder()
                        .setTraceId(AdapterUtils.traceId(0, 1))
                        .setSpanId(AdapterUtils.spanId(2))
                        .setFlags(1)
                        .setTraceState("first-trace-state")
                        .build(),
                Span.Link.newBuilder()
                        .setTraceId(AdapterUtils.traceId(3, 4))
                        .setSpanId(AdapterUtils.spanId(5))
                        .setFlags(2)
                        .setTraceState("second-trace-state")
                        .build()
        );

        protoLinksAdapter = new ProtoLinksAdapter();
        protoLinksAdapter.recycle(protoLinks);
    }

    @Test
    void has() {

        assertTrue(protoLinksAdapter.has(0, 1, 2));
        assertTrue(protoLinksAdapter.has(3, 4, 5));
        assertFalse(protoLinksAdapter.has(0, 0, 0));

    }

    @Test
    void get() {
        assertEquals(2, protoLinksAdapter.getSize());

        ProtoLinkAdapter protoLinkAdapter = protoLinksAdapter.get(0, 1, 2);

        assertNotNull(protoLinkAdapter);
        assertEquals(0, protoLinkAdapter.getTraceIdHigh());
        assertEquals(1, protoLinkAdapter.getTraceIdLow());
        assertEquals(2, protoLinkAdapter.getSpanId());
        assertEquals("first-trace-state", protoLinkAdapter.getTraceState());


        protoLinkAdapter = protoLinksAdapter.get(3, 4, 5);

        assertNotNull(protoLinkAdapter);
        assertEquals(3, protoLinkAdapter.getTraceIdHigh());
        assertEquals(4, protoLinkAdapter.getTraceIdLow());
        assertEquals(5, protoLinkAdapter.getSpanId());
        assertEquals("second-trace-state", protoLinkAdapter.getTraceState());

        assertNull(protoLinksAdapter.get(0, 0, 0));
    }

    @Test
    void add() {

        assertEquals(2, protoLinksAdapter.getSize());

        assertFalse(protoLinksAdapter.has(6, 7, 8));

        protoLinksAdapter.add(6, 7, 8, "new-link-trace-state", Map.of("str-key", "str-value"));

        assertEquals(3, protoLinksAdapter.getSize());
        assertTrue(protoLinksAdapter.has(6, 7, 8));
        ProtoLinkAdapter protoLinkAdapter = protoLinksAdapter.get(6, 7, 8);

        assertNotNull(protoLinkAdapter);
        assertEquals(6, protoLinkAdapter.getTraceIdHigh());
        assertEquals(7, protoLinkAdapter.getTraceIdLow());
        assertEquals(8, protoLinkAdapter.getSpanId());
        assertEquals("new-link-trace-state", protoLinkAdapter.getTraceState());

        ProtoAttributesAdapter protoAttributesAdapter = protoLinkAdapter.getAttributes();

        assertEquals(1, protoAttributesAdapter.getSize());
        assertTrue(protoAttributesAdapter.containsKey("str-key"));
        assertTrue(protoAttributesAdapter.isString("str-key"));
        assertEquals("str-value", protoAttributesAdapter.getAsString("str-key"));

    }

    @Test
    void remove() {

        assertEquals(2, protoLinksAdapter.getSize());
        assertTrue(protoLinksAdapter.has(0, 1, 2));

        assertTrue(protoLinksAdapter.remove(0, 1, 2));
        assertEquals(1, protoLinksAdapter.getSize());
        assertFalse(protoLinksAdapter.has(0, 1, 2));

        assertFalse(protoLinksAdapter.remove(0, 0, 0));
        assertEquals(1, protoLinksAdapter.getSize());

    }


    @Test
    void getUpdated_notUpdatable() {

        protoLinksAdapter = new ProtoLinksAdapter();
        protoLinksAdapter.recycle(protoLinks);

        assertSame(protoLinks, protoLinksAdapter.getUpdated());

    }


    @Test
    void getUpdated_notingUpdated() {

        protoLinksAdapter = new ProtoLinksAdapter();
        protoLinksAdapter.recycle(protoLinks);

        assertSame(protoLinks, protoLinksAdapter.getUpdated());

    }

    @Test
    void getUpdated_listChanged() {

        protoLinksAdapter.remove(0, 1, 2);


        List<Span.Link> actual = protoLinksAdapter.getUpdated();
        assertNotSame(protoLinks, actual);

        assertEquals(1, actual.size());
        Span.Link protoLink = actual.get(0);

        assertEquals(3, AdapterUtils.traceIdHigh(protoLink.getTraceId().toByteArray()));
        assertEquals(4, AdapterUtils.traceIdLow(protoLink.getTraceId().toByteArray()));
        assertEquals(5, AdapterUtils.spanId(protoLink.getSpanId().toByteArray()));
        assertEquals("second-trace-state", protoLink.getTraceState());

    }


    @Test
    void getUpdated_listContentChanged() {

        protoLinksAdapter.get(0, 1, 2).setDroppedAttributesCount(3);

        List<Span.Link> actual = protoLinksAdapter.getUpdated();
        assertNotSame(protoLinks, actual);

        assertEquals(2, actual.size());

        Span.Link protoLink= actual.get(0);

        assertNotNull(protoLink);
        assertEquals(0, AdapterUtils.traceIdHigh(protoLink.getTraceId().toByteArray()));
        assertEquals(1, AdapterUtils.traceIdLow(protoLink.getTraceId().toByteArray()));
        assertEquals(2, AdapterUtils.spanId(protoLink.getSpanId().toByteArray()));
        assertEquals("first-trace-state", protoLink.getTraceState());

        protoLink= actual.get(1);

        assertNotNull(protoLink);
        assertEquals(3, AdapterUtils.traceIdHigh(protoLink.getTraceId().toByteArray()));
        assertEquals(4, AdapterUtils.traceIdLow(protoLink.getTraceId().toByteArray()));
        assertEquals(5, AdapterUtils.spanId(protoLink.getSpanId().toByteArray()));
        assertEquals("second-trace-state", protoLink.getTraceState());

    }

}