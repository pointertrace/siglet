package com.siglet.data.adapter;

import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProtoInstrumentationScopeAdapterTest {

    ProtoInstrumentationScopeAdapter protoInstrumentationScopeAdapter;

    @BeforeEach
    void setUp() {

        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder()
                .setName("name-value")
                .setVersion("version-value")
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

        protoInstrumentationScopeAdapter = new ProtoInstrumentationScopeAdapter(instrumentationScope, true);

    }

    @Test
    public void get() {

        assertEquals("name-value", protoInstrumentationScopeAdapter.getName());
        assertEquals("version-value", protoInstrumentationScopeAdapter.getVersion());
        assertEquals(2, protoInstrumentationScopeAdapter.getDroppedAttributesCount());

    }

    @Test
    public void setAndGet() {

        protoInstrumentationScopeAdapter.setName("new-name-value");
        protoInstrumentationScopeAdapter.setVersion("new-version-value");
        protoInstrumentationScopeAdapter.setDroppedAttributesCount(3);

        assertEquals("new-name-value", protoInstrumentationScopeAdapter.getName());
        assertEquals("new-version-value", protoInstrumentationScopeAdapter.getVersion());
        assertEquals(3, protoInstrumentationScopeAdapter.getDroppedAttributesCount());
    }

    @Test
    public void attributesGet() {

        ProtoAttributesAdapter protoAttributesAdapter = protoInstrumentationScopeAdapter.getAttributes();

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

        ProtoAttributesAdapter protoAttributesAdapter = protoInstrumentationScopeAdapter.getAttributes();

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
    public void changeNotUpdatable() {
        protoInstrumentationScopeAdapter = new ProtoInstrumentationScopeAdapter(
                InstrumentationScope.newBuilder().build(), false);

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoInstrumentationScopeAdapter.setName("new-name-value");
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoInstrumentationScopeAdapter.setVersion("new-version-value");
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoInstrumentationScopeAdapter.setDroppedAttributesCount(3);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoInstrumentationScopeAdapter.getAttributes().remove("key");
        });
    }


}