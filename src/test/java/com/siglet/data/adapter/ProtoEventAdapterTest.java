package com.siglet.data.adapter;

import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProtoEventAdapterTest {

    ProtoEventAdapter protoEventAdapter;

    @BeforeEach
    void setUp() {
        Span.Event protoEvent = Span.Event.newBuilder()
                .setName("event-name")
                .setTimeUnixNano(1)
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

        protoEventAdapter = new ProtoEventAdapter(protoEvent, true);

    }

    @Test
    public void get() {

        assertEquals("event-name", protoEventAdapter.getName());
        assertEquals(1, protoEventAdapter.getTimeUnixNano());
        assertEquals(2, protoEventAdapter.getDroppedAttributesCount());
        assertFalse(protoEventAdapter.isChanged());

    }

    @Test
    public void setAndGet() {

        protoEventAdapter.setName("new-event-name");
        protoEventAdapter.setTimeUnixNano(2);
        protoEventAdapter.setDroppedAttributesCount(3);

        assertEquals("new-event-name", protoEventAdapter.getName());
        assertEquals(2, protoEventAdapter.getTimeUnixNano());
        assertEquals(3, protoEventAdapter.getDroppedAttributesCount());
        assertTrue(protoEventAdapter.isChanged());
    }

    @Test
    public void changeNonUpdatable() {
        protoEventAdapter = new ProtoEventAdapter(Span.Event.newBuilder().build(), false);


        assertThrowsExactly(IllegalStateException.class, () -> {
            protoEventAdapter.setName("new-event-name");
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoEventAdapter.setTimeUnixNano(2);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoEventAdapter.setDroppedAttributesCount(3);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoEventAdapter.setDroppedAttributesCount(3);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoEventAdapter.getAttributes().set("any", "any-value");
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoEventAdapter.getAttributes().remove("any");
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoEventAdapter.getAttributes().remove("any");
        });

        assertTrue(protoEventAdapter.isChanged());
    }

    @Test
    public void attributesGet() {

        ProtoAttributesAdapter protoAttributesAdapter = protoEventAdapter.getAttributes();

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

        ProtoAttributesAdapter protoAttributesAdapter = protoEventAdapter.getAttributes();

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
}