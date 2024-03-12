package com.siglet.data.adapter;

import com.google.protobuf.ByteString;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProtoLinkAdapterTest {

    Span.Link protoLink;
    private ProtoLinkAdapter protoLinkAdapter;

    @BeforeEach
    void setUp() {

        protoLink = Span.Link.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(0,1)))
                        .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(2)))
                .setFlags(1)
                .setTraceState("trace-state")
                .setDroppedAttributesCount(2)
                .addAttributes(KeyValue.newBuilder()
                        .setKey("str-attribute")
                        .setValue(AnyValue.newBuilder().setStringValue("str-attribute-value").build())
                        .build())
                .addAttributes(KeyValue.newBuilder()
                        .setKey("long-attribute")
                        .setValue(AnyValue.newBuilder().setIntValue(10L).build())
                        .build())
        .build();

        protoLinkAdapter = new ProtoLinkAdapter(protoLink, true);

    }

    @Test
    public void get() {

        assertEquals(0, protoLinkAdapter.getTraceIdHigh());
        assertEquals(1, protoLinkAdapter.getTraceIdLow());
        assertEquals(2, protoLinkAdapter.getSpanId());
        assertEquals(1, protoLinkAdapter.getFlags());
        assertEquals("trace-state", protoLinkAdapter.getTraceState());
        assertEquals(2, protoLinkAdapter.getDroppedAttributesCount());

    }

    @Test
    public void setAndGet() {
        protoLinkAdapter.setTraceId(3,4);
        protoLinkAdapter.setSpanId(5);
        protoLinkAdapter.setFlags(2);
        protoLinkAdapter.setTraceState("new-trace-state");
        protoLinkAdapter.setDroppedAttributesCount(3);

        assertEquals(3, protoLinkAdapter.getTraceIdHigh());
        assertEquals(4, protoLinkAdapter.getTraceIdLow());
        assertEquals(5, protoLinkAdapter.getSpanId());
        assertEquals(2, protoLinkAdapter.getFlags());
        assertEquals("new-trace-state", protoLinkAdapter.getTraceState());
        assertEquals(3, protoLinkAdapter.getDroppedAttributesCount());

    }

    @Test
    public void attributesGet() {

        ProtoAttributesAdapter protoAttributesAdapter = protoLinkAdapter.getAttributes();

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

        assertFalse(protoAttributesAdapter.isUpdated());
    }


    @Test
    public void attributesChangeAndGet() {

        ProtoAttributesAdapter protoAttributesAdapter = protoLinkAdapter.getAttributes();

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

        assertTrue(protoAttributesAdapter.isUpdated());
    }


    @Test
    public void changeNonUpdatable() {

        protoLinkAdapter = new ProtoLinkAdapter(Span.Link.newBuilder().build(), false);

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoLinkAdapter.setTraceId(3,4);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoLinkAdapter.setSpanId(5);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoLinkAdapter.setFlags(2);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoLinkAdapter.setTraceState("new-trace-state");
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoLinkAdapter.setDroppedAttributesCount(3);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoLinkAdapter.getAttributes().remove("str-key");
        });
    }


    @Test
    public void getUpdate_notUpdatable() {

        Span.Link actualProtoLink = Span.Link.newBuilder().build();
        protoLinkAdapter = new ProtoLinkAdapter(actualProtoLink, false);

        assertSame(actualProtoLink, protoLinkAdapter.getUpdated());

    }

    @Test
    public void getUpdated_nothingUpdated() {

        assertSame(protoLink, protoLinkAdapter.getUpdated());
        assertSame(protoLink.getAttributesList(), protoLinkAdapter.getUpdated().getAttributesList());

    }

    @Test
    public void getUpdated_onlyLinkUpdated() {

        protoLinkAdapter.setTraceId(3,4);
        protoLinkAdapter.setSpanId(5);

        Span.Link actual = protoLinkAdapter.getUpdated();
        assertNotSame(protoLink, actual);
        assertSame(protoLink.getAttributesList(), actual.getAttributesList());
        assertEquals(3, AdapterUtils.traceIdHigh(actual.getTraceId().toByteArray()));
        assertEquals(4, AdapterUtils.traceIdLow(actual.getTraceId().toByteArray()));
        assertEquals(5, AdapterUtils.spanId(actual.getSpanId().toByteArray()));

    }


    @Test
    public void getUpdated_onlyPropertiesUpdated() {

        protoLinkAdapter.getAttributes().set("bool-attribute", true);

        Span.Link actual = protoLinkAdapter.getUpdated();
        assertNotSame(protoLink, actual);
        assertNotSame(protoLink.getAttributesList(), actual.getAttributesList());

        assertEquals(3, actual.getAttributesList().size());
        Map<String, Object> attrAsMap = AdapterUtils.keyValueListToMap(actual.getAttributesList());
        assertEquals(3, attrAsMap.size());
        assertTrue(attrAsMap.containsKey("str-attribute"));
        assertTrue(attrAsMap.containsKey("long-attribute"));
        assertTrue(attrAsMap.containsKey("bool-attribute"));

    }

    @Test
    public void getUpdated_LinkAndPropertiesUpdated() {

        protoLinkAdapter.setTraceId(3,4);
        protoLinkAdapter.setSpanId(5);
        protoLinkAdapter.getAttributes().set("bool-attribute", true);


        Span.Link actual = protoLinkAdapter.getUpdated();
        assertNotSame(protoLink, actual);
        assertNotSame(protoLink.getAttributesList(), actual.getAttributesList());
        assertEquals(3, AdapterUtils.traceIdHigh(actual.getTraceId().toByteArray()));
        assertEquals(4, AdapterUtils.traceIdLow(actual.getTraceId().toByteArray()));
        assertEquals(5, AdapterUtils.spanId(actual.getSpanId().toByteArray()));

        assertEquals(3, actual.getAttributesList().size());
        Map<String, Object> attrAsMap = AdapterUtils.keyValueListToMap(actual.getAttributesList());
        assertEquals(3, attrAsMap.size());
        assertTrue(attrAsMap.containsKey("str-attribute"));
        assertTrue(attrAsMap.containsKey("long-attribute"));
        assertTrue(attrAsMap.containsKey("bool-attribute"));

    }

}