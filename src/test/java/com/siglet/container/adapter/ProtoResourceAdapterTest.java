package com.siglet.container.adapter;

import com.siglet.SigletError;
import com.siglet.container.adapter.common.ProtoAttributesAdapter;
import com.siglet.container.adapter.common.ProtoResourceAdapter;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProtoResourceAdapterTest {

    private Resource protoResource;

    private ProtoResourceAdapter protoResourceAdapter;

    @BeforeEach
    void setUp() {
        protoResource = Resource.newBuilder()
                .setDroppedAttributesCount(1)
                .addAttributes(KeyValue.newBuilder()
                        .setKey("str-attribute")
                        .setValue(AnyValue.newBuilder().setStringValue("str-attribute-value").build())
                        .build())
                .addAttributes(KeyValue.newBuilder()
                        .setKey("long-attribute")
                        .setValue(AnyValue.newBuilder().setIntValue(10).build())
                        .build())
                .build();

        protoResourceAdapter = new ProtoResourceAdapter(protoResource);

    }

    @Test
    void get() {

        assertEquals(1, protoResourceAdapter.getDroppedAttributesCount());

    }

    @Test
    void setAndGet() {
        protoResourceAdapter.setDroppedAttributesCount(2);

        assertEquals(2, protoResourceAdapter.getDroppedAttributesCount());

        assertTrue(protoResourceAdapter.isUpdated());

    }


    @Test
    void attributesGet() {

        ProtoAttributesAdapter protoAttributesAdapter = protoResourceAdapter.getAttributes();

        assertTrue(protoAttributesAdapter.containsKey("str-attribute"));
        assertTrue(protoAttributesAdapter.isString("str-attribute"));
        assertEquals("str-attribute-value", protoAttributesAdapter.getAsString("str-attribute"));

        assertEquals(10L, protoAttributesAdapter.getAsLong("long-attribute"));

        assertFalse(protoAttributesAdapter.isUpdated());
    }


    @Test
    void attributesChangeAndGet() {

        ProtoAttributesAdapter protoAttributesAdapter = protoResourceAdapter.getAttributes();

        protoAttributesAdapter.set("str-attribute", "new-str-attribute-value");
        protoAttributesAdapter.set("bool-attribute", true);
        protoAttributesAdapter.remove("long-attribute");

        assertTrue(protoAttributesAdapter.containsKey("str-attribute"));
        assertTrue(protoAttributesAdapter.isString("str-attribute"));
        assertEquals("new-str-attribute-value", protoAttributesAdapter.getAsString("str-attribute"));

        assertTrue(protoAttributesAdapter.getAsBoolean("bool-attribute"));

        assertTrue(protoResourceAdapter.isUpdated());
    }

    @Test
    void getUpdated_notUpdatable() {

        Resource actualProtoResource = Resource.newBuilder().build();
        protoResourceAdapter = new ProtoResourceAdapter(actualProtoResource);

        assertSame(actualProtoResource, protoResourceAdapter.getUpdated());

    }

    @Test
    void getUpdated_nothingUpdated() {

        assertSame(protoResource, protoResourceAdapter.getUpdated());
        assertSame(protoResource.getAttributesList(), protoResourceAdapter.getUpdated().getAttributesList());

    }

    @Test
    void getUpdated_onlyResourceUpdated() {

        protoResourceAdapter.setDroppedAttributesCount(2);

        Resource actual = protoResourceAdapter.getUpdated();
        assertNotSame(protoResource, actual);
        assertSame(protoResource.getAttributesList(), actual.getAttributesList());
        assertEquals(2, actual.getDroppedAttributesCount());

    }


    @Test
    void getUpdated_onlyPropertiesUpdated() {

        protoResourceAdapter.getAttributes().set("bool-attribute", true);

        Resource actual = protoResourceAdapter.getUpdated();
        assertNotSame(protoResource, actual);
        assertNotSame(protoResource.getAttributesList(), actual.getAttributesList());
        assertEquals(1, actual.getDroppedAttributesCount());
        assertEquals(3, actual.getAttributesList().size());
        Map<String, Object> attrAsMap = AdapterUtils.keyValueListToMap(actual.getAttributesList());
        assertEquals(3, attrAsMap.size());
        assertTrue(attrAsMap.containsKey("str-attribute"));
        assertTrue(attrAsMap.containsKey("long-attribute"));
        assertTrue(attrAsMap.containsKey("bool-attribute"));


    }

    @Test
    void getUpdated_ResourceAndPropertiesUpdated() {

        protoResourceAdapter.setDroppedAttributesCount(2);
        protoResourceAdapter.getAttributes().set("bool-attribute", true);

        Resource actual = protoResourceAdapter.getUpdated();
        assertNotSame(protoResource, actual);
        assertNotSame(protoResource.getAttributesList(), actual.getAttributesList());
        assertEquals(2, actual.getDroppedAttributesCount());

        assertEquals(3, actual.getAttributesList().size());
        Map<String, Object> attrAsMap = AdapterUtils.keyValueListToMap(actual.getAttributesList());
        assertEquals(3, attrAsMap.size());
        assertTrue(attrAsMap.containsKey("str-attribute"));
        assertTrue(attrAsMap.containsKey("long-attribute"));
        assertTrue(attrAsMap.containsKey("bool-attribute"));

    }

}