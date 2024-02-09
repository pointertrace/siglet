package com.siglet.data.adapter;

import com.google.protobuf.ByteString;
import io.opentelemetry.proto.common.v1.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AdapterUtilsTest {


    @Test
    public void anyValueToObject_stringValue() {

        AnyValue actual = AnyValue.newBuilder().setStringValue("value").build();

        assertEquals("value", AdapterUtils.anyValueToObject(actual));
    }

    @Test
    public void anyValueToObject_booleanValue() {

        AnyValue actual = AnyValue.newBuilder().setBoolValue(true).build();

        assertEquals(true, AdapterUtils.anyValueToObject(actual));
    }

    @Test
    public void anyValueToObject_intValue() {

        AnyValue actual = AnyValue.newBuilder().setIntValue(10L).build();

        assertEquals(10L, AdapterUtils.anyValueToObject(actual));
    }

    @Test
    public void anyValueToObject_doubleValue() {

        AnyValue actual = AnyValue.newBuilder().setDoubleValue(2.5).build();

        assertEquals(2.5, AdapterUtils.anyValueToObject(actual));
    }

    @Test
    public void anyValueToObject_arrayValueValue() {

        ArrayValue arrayValue = ArrayValue.newBuilder()
                .addValues(AnyValue.newBuilder().setStringValue("value").build())
                .addValues(AnyValue.newBuilder().setIntValue(20L).build())
                .build();

        AnyValue actual = AnyValue.newBuilder().setArrayValue(arrayValue).build();

        Object value = AdapterUtils.anyValueToObject(actual);

        Assertions.assertNotNull(value);
        Assertions.assertTrue(value instanceof Object[]);
        assertArrayEquals(new Object[]{"value", 20L}, ((Object[]) value));
    }

    @Test
    public void anyValueToObject_kvListValue() {

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
        Assertions.assertTrue(value instanceof List);
        assertEquals(2,((List<?>) value).size());
        List<Object> asList = (List<Object>) value;

        Assertions.assertNotNull(asList.get(0));
        Assertions.assertTrue(asList.get(0)  instanceof Map.Entry);
        assertEquals("key-1",((Map.Entry<?,?>) asList.get(0)).getKey());
        assertEquals("value-1",((Map.Entry<?,?>) asList.get(0)).getValue());

        Assertions.assertNotNull(asList.get(1));
        Assertions.assertTrue(asList.get(1)  instanceof Map.Entry);
        assertEquals("key-2",((Map.Entry<?,?>) asList.get(1)).getKey());
        assertEquals(20L,((Map.Entry<?,?>) asList.get(1)).getValue());
    }

    @Test
    public void anyValueToObject_bytes() {


        AnyValue actual = AnyValue.newBuilder().
                setBytesValue(ByteString.copyFrom("value", StandardCharsets.UTF_8))
                .build();


        Object value = AdapterUtils.anyValueToObject(actual);

        Assertions.assertNotNull(value);
        Assertions.assertTrue(value.getClass().isArray());
        Assertions.assertSame(value.getClass().getComponentType(), byte.class);
        assertEquals( "value",new String((byte[]) value,StandardCharsets.UTF_8));
    }

    @Test
    public void anyValueToObject_null() {

        AnyValue actual = AnyValue.newBuilder().build();

        Assertions.assertNull(AdapterUtils.anyValueToObject(actual));
    }
}