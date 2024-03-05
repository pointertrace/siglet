package com.siglet.data.adapter;

import com.google.protobuf.ByteString;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProtoLinksAdapterTest {

    private ProtoLinksAdapter protoLinksAdapter;

    @BeforeEach
    void setUp() {
        List<Span.Link> protoLinks = List.of(
                Span.Link.newBuilder()
                        .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(0, 1)))
                        .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(2)))
                        .setFlags(1)
                        .setTraceState("first-trace-state")
                        .build(),
                Span.Link.newBuilder()
                        .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(3, 4)))
                        .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(5)))
                        .setFlags(2)
                        .setTraceState("second-trace-state")
                        .build()
        );

        protoLinksAdapter = new ProtoLinksAdapter(protoLinks, true);
    }

    @Test
    public void has() {

        assertTrue(protoLinksAdapter.has(0,1,2));
        assertTrue(protoLinksAdapter.has(3,4,5));
        assertFalse(protoLinksAdapter.has(0,0,0));

    }

    @Test
    public void get() {
        assertEquals(2, protoLinksAdapter.size());

        ProtoLinkAdapter protoLinkAdapter = protoLinksAdapter.get(0,1,2);

        assertNotNull(protoLinkAdapter);
        assertEquals(0,protoLinkAdapter.getTraceIdHigh());
        assertEquals(1, protoLinkAdapter.getTraceIdLow());
        assertEquals(2, protoLinkAdapter.getSpanId());
        assertEquals("first-trace-state", protoLinkAdapter.getTraceState());


        protoLinkAdapter = protoLinksAdapter.get(3,4,5);

        assertNotNull(protoLinkAdapter);
        assertEquals(3,protoLinkAdapter.getTraceIdHigh());
        assertEquals(4, protoLinkAdapter.getTraceIdLow());
        assertEquals(5, protoLinkAdapter.getSpanId());
        assertEquals("second-trace-state", protoLinkAdapter.getTraceState());

        assertNull(protoLinksAdapter.get(0,0,0));
    }

    @Test
    public void add() {

        assertEquals(2, protoLinksAdapter.size());

        assertFalse(protoLinksAdapter.has(6,7,8));

        protoLinksAdapter.add(6,7,8,"new-link-trace-state", Map.of("str-key","str-value"));

        assertEquals(3, protoLinksAdapter.size());
        assertTrue(protoLinksAdapter.has(6,7,8));
        ProtoLinkAdapter protoLinkAdapter = protoLinksAdapter.get(6,7,8);

        assertNotNull(protoLinkAdapter);
        assertEquals(6,protoLinkAdapter.getTraceIdHigh());
        assertEquals(7, protoLinkAdapter.getTraceIdLow());
        assertEquals(8, protoLinkAdapter.getSpanId());
        assertEquals("new-link-trace-state", protoLinkAdapter.getTraceState());

        ProtoAttributesAdapter protoAttributesAdapter = protoLinkAdapter.getAttributes();

        assertEquals(1, protoAttributesAdapter.size());
        assertTrue(protoAttributesAdapter.has("str-key"));
        assertTrue(protoAttributesAdapter.isString("str-key"));
        assertEquals("str-value", protoAttributesAdapter.getAsString("str-key"));

    }

    @Test
    public void remove() {

        assertEquals(2, protoLinksAdapter.size());
        assertTrue(protoLinksAdapter.has(0,1,2));

        assertTrue(protoLinksAdapter.remove(0,1,2));
        assertEquals(1, protoLinksAdapter.size());
        assertFalse(protoLinksAdapter.has(0,1,2));

        assertFalse(protoLinksAdapter.remove(0,0,0));
        assertEquals(1, protoLinksAdapter.size());

    }


    @Test
    public void changeNonUpdatable() {

        protoLinksAdapter = new ProtoLinksAdapter(Collections.emptyList(), false);

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoLinksAdapter.add(0, 0, 0, "state", Collections.emptyMap());
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoLinksAdapter.remove(0, 0, 0);
        });

    }

}