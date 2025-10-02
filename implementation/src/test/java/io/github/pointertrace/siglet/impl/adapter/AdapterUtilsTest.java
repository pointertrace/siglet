package io.github.pointertrace.siglet.impl.adapter;

import com.google.protobuf.ByteString;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.ArrayValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.common.v1.KeyValueList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AdapterUtilsTest {


    @Test
    void anyValueToObject_stringValue() {

        AnyValue actual = AnyValue.newBuilder().setStringValue("value").build();

        assertEquals("value", AdapterUtils.anyValueToObject(actual));
    }

    @Test
    void anyValueToObject_booleanValue() {

        AnyValue actual = AnyValue.newBuilder().setBoolValue(true).build();

        assertEquals(true, AdapterUtils.anyValueToObject(actual));
    }

    @Test
    void anyValueToObject_intValue() {

        AnyValue actual = AnyValue.newBuilder().setIntValue(10L).build();

        assertEquals(10L, AdapterUtils.anyValueToObject(actual));
    }

    @Test
    void anyValueToObject_doubleValue() {

        AnyValue actual = AnyValue.newBuilder().setDoubleValue(2.5).build();

        assertEquals(2.5, AdapterUtils.anyValueToObject(actual));
    }

    @Test
    void anyValueToObject_arrayValueValue() {

        ArrayValue arrayValue = ArrayValue.newBuilder()
                .addValues(AnyValue.newBuilder().setStringValue("value").build())
                .addValues(AnyValue.newBuilder().setIntValue(20L).build())
                .build();

        AnyValue actual = AnyValue.newBuilder().setArrayValue(arrayValue).build();

        Object value = AdapterUtils.anyValueToObject(actual);

        Assertions.assertNotNull(value);
        assertInstanceOf(Object[].class, value);
        assertArrayEquals(new Object[]{"value", 20L}, ((Object[]) value));
    }

    @Test
    void anyValueToObject_kvListValue() {

        KeyValueList keyValueList = KeyValueList.newBuilder()
                .addValues(KeyValue.newBuilder()
                        .setKey("key-1")
                        .setValue(AnyValue.newBuilder().setStringValue("value-1").build()))
                .addValues(KeyValue.newBuilder()
                        .setKey("key-2")
                        .setValue(AnyValue.newBuilder().setIntValue(20L))
                        .build())
                .build();

        AnyValue actual = AnyValue.newBuilder().setKvlistValue(keyValueList).build();

        Object value = AdapterUtils.anyValueToObject(actual);


        Assertions.assertNotNull(value);
        assertInstanceOf(List.class, value);
        assertEquals(2, ((List<?>) value).size());
        List<Object> asList = (List<Object>) value;

        Assertions.assertNotNull(asList.get(0));
        assertInstanceOf(Map.Entry.class, asList.get(0));
        assertEquals("key-1", ((Map.Entry<?, ?>) asList.get(0)).getKey());
        assertEquals("value-1", ((Map.Entry<?, ?>) asList.get(0)).getValue());

        Assertions.assertNotNull(asList.get(1));
        assertInstanceOf(Map.Entry.class, asList.get(1));
        assertEquals("key-2", ((Map.Entry<?, ?>) asList.get(1)).getKey());
        assertEquals(20L, ((Map.Entry<?, ?>) asList.get(1)).getValue());
    }

    @Test
    void anyValueToObject_bytes() {

        AnyValue actual = AnyValue.newBuilder().
                setBytesValue(ByteString.copyFrom("value", StandardCharsets.UTF_8))
                .build();


        Object value = AdapterUtils.anyValueToObject(actual);

        Assertions.assertNotNull(value);
        assertTrue(value.getClass().isArray());
        Assertions.assertSame(value.getClass().getComponentType(), byte.class);
        assertEquals("value", new String((byte[]) value, StandardCharsets.UTF_8));
    }

    @Test
    void anyValueToObject_null() {

        AnyValue actual = AnyValue.newBuilder().build();

        Assertions.assertNull(AdapterUtils.anyValueToObject(actual));
    }

    @Test
    void mapToKeyValueList() {
        Map<String, Object> map = new HashMap<>();

        map.put("str-key", "str-value");
        map.put("long-key", 10L);

        List<KeyValue> kv = AdapterUtils.mapToKeyValueList(map);

        assertEquals(2, kv.size());
        assertTrue(kv.contains(KeyValue.newBuilder()
                .setKey("str-key")
                .setValue(AnyValue.newBuilder().setStringValue("str-value").build())
                .build()));
        assertTrue(kv.contains(KeyValue.newBuilder()
                .setKey("long-key")
                .setValue(AnyValue.newBuilder().setIntValue(10L).build())
                .build()));

    }

    @Test
    void keyValueListToMap() {
        List<KeyValue> kvl = KeyValueList.newBuilder()
                .addValues(KeyValue.newBuilder()
                        .setKey("str-key").
                        setValue(AnyValue.newBuilder().setStringValue("str-value").build()).build())
                .addValues(KeyValue.newBuilder()
                        .setKey("long-key")
                        .setValue(AnyValue.newBuilder().setIntValue(10L).build()).build())
                .build()
                .getValuesList();


        Map<String,Object> map = AdapterUtils.keyValueListToMap(kvl);

        assertEquals(2, map.size());

        assertTrue(map.containsKey("str-key"));
        assertEquals("str-value", map.get("str-key"));

        assertTrue(map.containsKey("long-key"));
        assertEquals(10L, map.get("long-key"));

    }
}