package com.siglet.data.adapter;

import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.common.v1.KeyValue.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdapterTest {

    KeyValue keyValue;

    Adapter<KeyValue, KeyValue.Builder> adapter;

    @BeforeEach
    public void setUp() {
        keyValue = KeyValue.newBuilder()
                .setKey("key")
                .setValue(AnyValue.newBuilder().setStringValue("value").build())
                .build();

        adapter = new Adapter<>(keyValue, KeyValue::toBuilder, KeyValue.Builder::build, true);
    }


    @Test
    public void getValue_message() {

        assertEquals("key", adapter.getValue(KeyValue::getKey, KeyValue.Builder::getKey));
        assertFalse(adapter.isUpdated());
    }


    @Test
    public void getValue_builder() {

        KeyValue.Builder builder = KeyValue.newBuilder().setKey("other-key");
        adapter = new Adapter<>(builder, KeyValue.Builder::build);

        assertEquals("other-key", adapter.getValue(KeyValue::getKey, KeyValue.Builder::getKey));
        assertTrue(adapter.isUpdated());
    }

    @Test
    public void setAndGetValue_message() {

        adapter.setValue(KeyValue.Builder::setKey, "new-value");
        assertEquals("new-value", adapter.getValue(KeyValue::getKey, KeyValue.Builder::getKey));
        assertTrue(adapter.isUpdated());
    }

    @Test
    public void setAndGetValue_builder() {

        KeyValue.Builder builder = KeyValue.newBuilder().setKey("other-key");

        adapter = new Adapter<>(builder, KeyValue.Builder::build);
        adapter.setValue(KeyValue.Builder::setKey, "new-value");

        assertEquals("new-value", adapter.getValue(KeyValue::getKey, KeyValue.Builder::getKey));
        assertTrue(adapter.isUpdated());

    }

    @Test
    public void getUpdated_messageNoChange() {

        assertSame(keyValue, adapter.getUpdated());

    }

    @Test
    public void getUpdated_builderNoChange() {

        KeyValue.Builder builder = KeyValue.newBuilder()
                .setKey("key")
                .setValue(AnyValue.newBuilder().setStringValue("value").build());

        adapter = new Adapter<>(builder, KeyValue.Builder::build);


        assertEquals(KeyValue.newBuilder()
                .setKey("key")
                .setValue(AnyValue.newBuilder().setStringValue("value").build())
                .build(), adapter.getUpdated());
        assertTrue(adapter.isUpdated());

    }

    @Test
    public void getUpdated_builderChange() {

        KeyValue.Builder builder = KeyValue.newBuilder()
                .setKey("key")
                .setValue(AnyValue.newBuilder().setStringValue("value").build());

        adapter = new Adapter<>(builder, KeyValue.Builder::build);
        adapter.setValue(KeyValue.Builder::setKey, "new-key");


        assertEquals(KeyValue.newBuilder()
                .setKey("new-key")
                .setValue(AnyValue.newBuilder().setStringValue("value").build())
                .build(), adapter.getUpdated());
        assertTrue(adapter.isUpdated());

    }

    @Test
    public void enrich() {

        AdapterWithBuilderEnrich adapter = new AdapterWithBuilderEnrich(keyValue);
        adapter.setValue(KeyValue.Builder::setKey,"new-value");


        assertEquals(KeyValue.newBuilder()
                .setKey("new-value.sufix")
                .setValue(AnyValue.newBuilder().setStringValue("value").build())
                .build(), adapter.getUpdated());


    }

   public static class AdapterWithBuilderEnrich extends Adapter<KeyValue, KeyValue.Builder> {

       public AdapterWithBuilderEnrich(KeyValue message) {
           super(message, KeyValue::toBuilder, KeyValue.Builder::build, true);
       }

       @Override
       protected void enrich(Builder builder) {
           builder.setKey(builder.getKey() + ".sufix");
       }
   }
}