package com.siglet.data.adapter;

import com.google.protobuf.ByteString;
import com.siglet.data.trace.SpanKind;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProtoSpanAdapterTest {

    private ProtoSpanAdapter protoSpanAdapter;

    @BeforeEach
    void setUp() {
        Span protoSpan = Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(0, 2)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(1)))
                .setParentSpanId(ByteString.copyFrom(AdapterUtils.spanId(3)))
                .setName("span-name")
                .setStartTimeUnixNano(1L)
                .setEndTimeUnixNano(2L)
                .setFlags(1)
                .setTraceState("trace-state")
                .setDroppedAttributesCount(1)
                .setDroppedEventsCount(2)
                .setDroppedLinksCount(3)
                .setKind(Span.SpanKind.SPAN_KIND_CLIENT)
                .addAttributes(KeyValue.newBuilder()
                        .setKey("str-attribute")
                        .setValue(AnyValue.newBuilder().setStringValue("str-attribute-value").build())
                        .build())
                .addAttributes(KeyValue.newBuilder()
                        .setKey("long-attribute")
                        .setValue(AnyValue.newBuilder().setIntValue(10L).build())
                        .build())
                .addLinks(Span.Link.newBuilder()
                        .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(4)))
                        .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(0L, 5L)))
                        .setTraceState("first-link-trace-state")
                        .setFlags(2)
                        .setDroppedAttributesCount(10)
                        .addAttributes(KeyValue.newBuilder()
                                .setKey("lnk-str-attribute")
                                .setValue(AnyValue.newBuilder().setStringValue("lnk-str-attribute-value").build())
                                .build())
                        .build())
                .addLinks(Span.Link.newBuilder()
                        .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(6)))
                        .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(0L, 7L)))
                        .setTraceState("second-link-trace-state")
                        .setFlags(3)
                        .setDroppedAttributesCount(20)
                        .build())
                .addEvents(Span.Event.newBuilder()
                        .setName("first-event-name")
                        .setTimeUnixNano(1)
                        .setDroppedAttributesCount(30)
                        .addAttributes(KeyValue.newBuilder()
                                .setKey("evt-str-attribute")
                                .setValue(AnyValue.newBuilder().setStringValue("evt-str-attribute-value").build())
                                .build())
                        .build())
                .addEvents(Span.Event.newBuilder()
                        .setName("second-event-name")
                        .setTimeUnixNano(2)
                        .setDroppedAttributesCount(40)
                        .build())
                .build();

        Resource resource = Resource.newBuilder()
                .setDroppedAttributesCount(2)
                .addAttributes(KeyValue.newBuilder()
                        .setKey("rs-str-attribute")
                        .setValue(AnyValue.newBuilder().setStringValue("rs-str-attribute-value").build())
                        .build())
                .build();

        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder()
                .setName("instrumentation-scope-name")
                .setVersion("instrumentation-scope-version")
                .setDroppedAttributesCount(3)
                .addAttributes(KeyValue.newBuilder()
                        .setKey("is-str-attribute")
                        .setValue(AnyValue.newBuilder().setStringValue("is-str-attribute-value").build())
                        .build())
                .build();

        protoSpanAdapter = new ProtoSpanAdapter(protoSpan, resource, instrumentationScope, true);

    }

    @Test
    public void setAndGet() {
        protoSpanAdapter.setTraceId(10L, 20L);
        protoSpanAdapter.setSpanId(10L);
        protoSpanAdapter.setParentSpanId(30L);
        protoSpanAdapter.setName("new-name");
        protoSpanAdapter.setStartTimeUnixNano(3L);
        protoSpanAdapter.setEndTimeUnixNano(4L);
        protoSpanAdapter.setFlags(2);
        protoSpanAdapter.setTraceState("new-trace-state");
        protoSpanAdapter.setDroppedAttributesCount(10);
        protoSpanAdapter.setDroppedEventsCount(20);
        protoSpanAdapter.setDroppedLinksCount(30);
        protoSpanAdapter.setKind(SpanKind.SERVER);


        assertEquals(protoSpanAdapter.getTraceIdHigh(), 10);
        assertEquals(protoSpanAdapter.getTraceIdLow(), 20);
        assertEquals(protoSpanAdapter.getSpanId(), 10L);
        assertEquals(protoSpanAdapter.getParentSpanId(), 30L);
        assertEquals(protoSpanAdapter.getName(), "new-name");
        assertEquals(protoSpanAdapter.getStartUnixNano(), 3L);
        assertEquals(protoSpanAdapter.getEndUnixNano(), 4L);
        assertEquals(protoSpanAdapter.getFlags(), 2);
        assertEquals(protoSpanAdapter.getTraceState(), "new-trace-state");
        assertEquals(protoSpanAdapter.getDroppedAttributesCount(), 10);
        assertEquals(protoSpanAdapter.getDroppedEventsCount(), 20);
        assertEquals(protoSpanAdapter.getDroppedLinksCount(), 30);
        assertEquals(protoSpanAdapter.getKind(), SpanKind.SERVER);

    }

    @Test
    public void setNonUpdatable() {
        protoSpanAdapter = new ProtoSpanAdapter(Span.newBuilder().build(), Resource.newBuilder().build(),
                InstrumentationScope.newBuilder().build(), false);

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoSpanAdapter.setTraceId(10L, 20L);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoSpanAdapter.setSpanId(10L);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoSpanAdapter.setParentSpanId(30L);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoSpanAdapter.setName("new-name");
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoSpanAdapter.setStartTimeUnixNano(3L);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoSpanAdapter.setEndTimeUnixNano(4L);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoSpanAdapter.setFlags(2);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoSpanAdapter.setTraceState("new-trace-state");
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoSpanAdapter.setDroppedAttributesCount(10);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoSpanAdapter.setDroppedEventsCount(20);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoSpanAdapter.setDroppedLinksCount(30);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoSpanAdapter.setKind(SpanKind.SERVER);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoSpanAdapter.getResource().setDroppedAttributesCount(1);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoSpanAdapter.getAttributes().remove("str-key");
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoSpanAdapter.getLinks().remove(0, 0, 0);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoSpanAdapter.getInstrumentationScope().setName("new-name");
        });
    }

    @Test
    public void setAndGetNotChangedValues() {
        protoSpanAdapter.setTraceId(10L, 20L);

        assertEquals(protoSpanAdapter.getSpanId(), 1L);
        assertEquals(protoSpanAdapter.getParentSpanId(), 3L);
        assertEquals(protoSpanAdapter.getName(), "span-name");
        assertEquals(protoSpanAdapter.getStartUnixNano(), 1L);
        assertEquals(protoSpanAdapter.getEndUnixNano(), 2L);
        assertEquals(protoSpanAdapter.getFlags(), 1);
        assertEquals(protoSpanAdapter.getTraceState(), "trace-state");
        assertEquals(protoSpanAdapter.getDroppedAttributesCount(), 1);
        assertEquals(protoSpanAdapter.getDroppedEventsCount(), 2);
        assertEquals(protoSpanAdapter.getDroppedLinksCount(), 3);
        assertEquals(protoSpanAdapter.getKind(), SpanKind.CLIENT);

    }

    @Test
    public void get() {

        assertEquals(protoSpanAdapter.getTraceIdHigh(), 0);
        assertEquals(protoSpanAdapter.getTraceIdLow(), 2);
        assertEquals(protoSpanAdapter.getSpanId(), 1L);
        assertEquals(protoSpanAdapter.getParentSpanId(), 3L);
        assertEquals(protoSpanAdapter.getName(), "span-name");
        assertEquals(protoSpanAdapter.getStartUnixNano(), 1L);
        assertEquals(protoSpanAdapter.getEndUnixNano(), 2L);
        assertEquals(protoSpanAdapter.getFlags(), 1);
        assertEquals(protoSpanAdapter.getTraceState(), "trace-state");
        assertEquals(protoSpanAdapter.getDroppedAttributesCount(), 1);
        assertEquals(protoSpanAdapter.getDroppedEventsCount(), 2);
        assertEquals(protoSpanAdapter.getDroppedLinksCount(), 3);
        assertEquals(protoSpanAdapter.getKind(), SpanKind.CLIENT);

    }

    @Test
    public void attributesGet() {

        ProtoAttributesAdapter protoAttributesAdapter = protoSpanAdapter.getAttributes();

        assertTrue(protoAttributesAdapter.has("str-attribute"));
        assertTrue(protoAttributesAdapter.isString("str-attribute"));
        assertEquals(protoAttributesAdapter.getAsString("str-attribute"), "str-attribute-value");

        Map<String, Object> attributesMap = protoAttributesAdapter.getAsMap();

        assertEquals(2, attributesMap.size());

        assertTrue(attributesMap.containsKey("str-attribute"));
        assertInstanceOf(String.class, attributesMap.get("str-attribute"));
        assertEquals(protoAttributesAdapter.getAsString("str-attribute"), "str-attribute-value");

        assertTrue(attributesMap.containsKey("long-attribute"));
        assertInstanceOf(Long.class, attributesMap.get("long-attribute"));
        assertEquals(10L, protoAttributesAdapter.getAsLong("long-attribute"));

        assertFalse(protoAttributesAdapter.isChanged());
    }


    @Test
    public void attributesChangeAndGet() {

        ProtoAttributesAdapter protoAttributesAdapter = protoSpanAdapter.getAttributes();

        protoAttributesAdapter.set("str-attribute", "new-str-attribute-value");
        protoAttributesAdapter.set("bool-attribute", true);
        protoAttributesAdapter.remove("long-attribute");

        assertTrue(protoAttributesAdapter.has("str-attribute"));
        assertTrue(protoAttributesAdapter.isString("str-attribute"));
        assertEquals(protoAttributesAdapter.getAsString("str-attribute"), "new-str-attribute-value");

        Map<String, Object> attributesMap = protoAttributesAdapter.getAsMap();
        assertEquals(2, attributesMap.size());

        assertTrue(attributesMap.containsKey("str-attribute"));
        assertInstanceOf(String.class, attributesMap.get("str-attribute"));
        assertEquals(protoAttributesAdapter.getAsString("str-attribute"), "new-str-attribute-value");

        assertTrue(attributesMap.containsKey("bool-attribute"));
        assertInstanceOf(Boolean.class, attributesMap.get("bool-attribute"));
        assertTrue(protoAttributesAdapter.getAsBoolean("bool-attribute"));

        assertTrue(protoAttributesAdapter.isChanged());
    }

    @Test
    public void linksGet() {

        ProtoLinksAdapter protoLinksAdapter = protoSpanAdapter.getLinks();

        assertEquals(2, protoLinksAdapter.size());
        assertTrue(protoLinksAdapter.has(0, 5, 4));

        ProtoLinkAdapter protoLinkAdapter = protoLinksAdapter.get(0, 5, 4);
        assertNotNull(protoLinkAdapter);
        assertEquals(0, protoLinkAdapter.getTraceIdHigh());
        assertEquals(5, protoLinkAdapter.getTraceIdLow());
        assertEquals(4, protoLinkAdapter.getSpanId());

        ProtoAttributesAdapter attributes = protoLinkAdapter.getAttributes();

        assertEquals(1, attributes.size());

        assertTrue(attributes.has("lnk-str-attribute"));
        assertTrue(attributes.isString("lnk-str-attribute"));
        assertEquals("lnk-str-attribute-value", attributes.getAsString("lnk-str-attribute"));

        protoLinkAdapter = protoLinksAdapter.get(0, 7, 6);
        assertNotNull(protoLinkAdapter);
        assertEquals(0, protoLinkAdapter.getTraceIdHigh());
        assertEquals(7, protoLinkAdapter.getTraceIdLow());
        assertEquals(6, protoLinkAdapter.getSpanId());


    }

    @Test
    public void eventsGet() {

        ProtoEventsAdapter protoEventsAdapter = protoSpanAdapter.getEvents();

        assertEquals(2, protoEventsAdapter.size());

        ProtoEventAdapter protoEventAdapter = protoEventsAdapter.get(0);
        assertNotNull(protoEventAdapter);
        assertEquals("first-event-name", protoEventAdapter.getName());

        ProtoAttributesAdapter attributes = protoEventAdapter.getAttributes();

        assertEquals(1, attributes.size());

        assertTrue(attributes.has("evt-str-attribute"));
        assertTrue(attributes.isString("evt-str-attribute"));
        assertEquals("evt-str-attribute-value", attributes.getAsString("evt-str-attribute"));

        protoEventAdapter = protoEventsAdapter.get(1);
        assertNotNull(protoEventAdapter);
        assertEquals("second-event-name", protoEventAdapter.getName());


    }

    @Test
    public void resourceGet() {

        ProtoResourceAdapter protoResourceAdapter = protoSpanAdapter.getResource();

        assertEquals(2, protoResourceAdapter.getDroppedAttributesCount());

        ProtoAttributesAdapter attributes = protoResourceAdapter.getAttributes();

        assertEquals(1, attributes.size());

        assertTrue(attributes.has("rs-str-attribute"));
        assertTrue(attributes.isString("rs-str-attribute"));
        assertEquals("rs-str-attribute-value", attributes.getAsString("rs-str-attribute"));

    }


    @Test
    public void instrumentationScopeGet() {
        ProtoInstrumentationScopeAdapter protoInstrumentationScopeAdapter =
                protoSpanAdapter.getInstrumentationScope();

        assertEquals("instrumentation-scope-name", protoInstrumentationScopeAdapter.getName());
        assertEquals("instrumentation-scope-version", protoInstrumentationScopeAdapter.getVersion());

        ProtoAttributesAdapter attributes = protoInstrumentationScopeAdapter.getAttributes();

        assertEquals(1, attributes.size());

        assertTrue(attributes.has("is-str-attribute"));
        assertTrue(attributes.isString("is-str-attribute"));
        assertEquals("is-str-attribute-value", attributes.getAsString("is-str-attribute"));
    }

}