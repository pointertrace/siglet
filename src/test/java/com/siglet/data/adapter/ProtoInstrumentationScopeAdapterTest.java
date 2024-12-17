package com.siglet.data.adapter;

import com.siglet.SigletError;
import com.siglet.data.adapter.common.ProtoAttributesAdapter;
import com.siglet.data.adapter.common.ProtoInstrumentationScopeAdapter;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProtoInstrumentationScopeAdapterTest {

    InstrumentationScope protoInstrumentationScope;
    ProtoInstrumentationScopeAdapter protoInstrumentationScopeAdapter;

    @BeforeEach
    void setUp() {

        protoInstrumentationScope = InstrumentationScope.newBuilder()
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

        protoInstrumentationScopeAdapter = new ProtoInstrumentationScopeAdapter(protoInstrumentationScope, true);

    }

    @Test
    void get() {

        assertEquals("name-value", protoInstrumentationScopeAdapter.getName());
        assertEquals("version-value", protoInstrumentationScopeAdapter.getVersion());
        assertEquals(2, protoInstrumentationScopeAdapter.getDroppedAttributesCount());

    }

    @Test
    void setAndGet() {

        protoInstrumentationScopeAdapter.setName("new-name-value");
        protoInstrumentationScopeAdapter.setVersion("new-version-value");
        protoInstrumentationScopeAdapter.setDroppedAttributesCount(3);

        assertEquals("new-name-value", protoInstrumentationScopeAdapter.getName());
        assertEquals("new-version-value", protoInstrumentationScopeAdapter.getVersion());
        assertEquals(3, protoInstrumentationScopeAdapter.getDroppedAttributesCount());
    }

    @Test
    void attributesGet() {

        ProtoAttributesAdapter protoAttributesAdapter = protoInstrumentationScopeAdapter.getAttributes();

        assertTrue(protoAttributesAdapter.containsKey("str-attribute"));
        assertTrue(protoAttributesAdapter.isString("str-attribute"));
        assertEquals("str-attribute-value",protoAttributesAdapter.getAsString("str-attribute"));

        assertEquals(10L, protoAttributesAdapter.getAsLong("long-attribute"));

        assertFalse(protoAttributesAdapter.isUpdated());
    }


    @Test
    void attributesChangeAndGet() {

        ProtoAttributesAdapter protoAttributesAdapter = protoInstrumentationScopeAdapter.getAttributes();

        protoAttributesAdapter.set("str-attribute", "new-str-attribute-value");
        protoAttributesAdapter.set("bool-attribute", true);
        protoAttributesAdapter.remove("long-attribute");

        assertTrue(protoAttributesAdapter.containsKey("str-attribute"));
        assertTrue(protoAttributesAdapter.isString("str-attribute"));
        assertEquals("new-str-attribute-value",protoAttributesAdapter.getAsString("str-attribute"));

        assertTrue(protoAttributesAdapter.getAsBoolean("bool-attribute"));

        assertTrue(protoAttributesAdapter.isUpdated());
    }


    @Test
    void changeNotUpdatable() {
        protoInstrumentationScopeAdapter = new ProtoInstrumentationScopeAdapter(
                InstrumentationScope.newBuilder().build(), false);

        assertThrowsExactly(SigletError.class, () -> {
            protoInstrumentationScopeAdapter.setName("new-name-value");
        });

        assertThrowsExactly(SigletError.class, () -> {
            protoInstrumentationScopeAdapter.setVersion("new-version-value");
        });

        assertThrowsExactly(SigletError.class, () -> {
            protoInstrumentationScopeAdapter.setDroppedAttributesCount(3);
        });

        assertThrowsExactly(SigletError.class, () -> {
            protoInstrumentationScopeAdapter.getAttributes().remove("key");
        });
    }

    @Test
    void getUpdate_notUpdatable() {

        InstrumentationScope actualProtoInstAdapt = InstrumentationScope.newBuilder().build();
        protoInstrumentationScopeAdapter = new ProtoInstrumentationScopeAdapter(actualProtoInstAdapt, false);

        assertSame(actualProtoInstAdapt, protoInstrumentationScopeAdapter.getUpdated());

    }

    @Test
    void getUpdated_nothingUpdated() {

        assertSame(protoInstrumentationScope, protoInstrumentationScopeAdapter.getUpdated());
        assertSame(protoInstrumentationScope.getAttributesList(),
                protoInstrumentationScopeAdapter.getUpdated().getAttributesList());

    }

    @Test
    void getUpdated_onlyResourceUpdated() {

        protoInstrumentationScopeAdapter.setName("new-name");
        protoInstrumentationScopeAdapter.setVersion("new-version");

        InstrumentationScope actual = protoInstrumentationScopeAdapter.getUpdated();
        assertNotSame(protoInstrumentationScope, actual);
        assertSame(protoInstrumentationScope.getAttributesList(), actual.getAttributesList());
        assertEquals("new-name", actual.getName());
        assertEquals("new-version", actual.getVersion());

    }


    @Test
    void getUpdated_onlyPropertiesUpdated() {

        protoInstrumentationScopeAdapter.getAttributes().set("bool-attribute", true);

        InstrumentationScope actual = protoInstrumentationScopeAdapter.getUpdated();
        assertNotSame(protoInstrumentationScope, actual);
        assertNotSame(protoInstrumentationScope.getAttributesList(), actual.getAttributesList());
        assertEquals("name-value", actual.getName());
        assertEquals("version-value", actual.getVersion());

        assertEquals(3, actual.getAttributesList().size());
        Map<String, Object> attrAsMap = AdapterUtils.keyValueListToMap(actual.getAttributesList());
        assertEquals(3, attrAsMap.size());
        assertTrue(attrAsMap.containsKey("str-attribute"));
        assertTrue(attrAsMap.containsKey("long-attribute"));
        assertTrue(attrAsMap.containsKey("bool-attribute"));

    }

    @Test
    void getUpdated_ResourceAndPropertiesUpdated() {

        protoInstrumentationScopeAdapter.setName("new-name");
        protoInstrumentationScopeAdapter.setVersion("new-version");

        protoInstrumentationScopeAdapter.getAttributes().set("bool-attribute", true);

        InstrumentationScope actual = protoInstrumentationScopeAdapter.getUpdated();
        assertNotSame(protoInstrumentationScope, actual);
        assertNotSame(protoInstrumentationScope.getAttributesList(), actual.getAttributesList());
        assertEquals("new-name", actual.getName());
        assertEquals("new-version", actual.getVersion());

        assertEquals(3, actual.getAttributesList().size());
        Map<String, Object> attrAsMap = AdapterUtils.keyValueListToMap(actual.getAttributesList());
        assertEquals(3, attrAsMap.size());
        assertTrue(attrAsMap.containsKey("str-attribute"));
        assertTrue(attrAsMap.containsKey("long-attribute"));
        assertTrue(attrAsMap.containsKey("bool-attribute"));

    }

}