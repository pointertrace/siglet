package com.siglet.container.adapter.metric;

import com.siglet.SigletError;
import com.siglet.container.adapter.AdapterUtils;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.Exemplar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProtoExemplarAdapterTest {

    private Exemplar protoExemplar;

    private ProtoExemplarAdapter protoExemplarAdapter;

    @BeforeEach
    void setUp() {
        protoExemplar = Exemplar.newBuilder()
                .setTimeUnixNano(10)
                .setAsInt(100)
                .setSpanId(AdapterUtils.spanId(10))
                .setTraceId(AdapterUtils.traceId(1, 2))
                .addAllFilteredAttributes(List.of(KeyValue.newBuilder()
                        .setKey("key")
                        .setValue(AnyValue.newBuilder().setStringValue("value").build())
                        .build()))
                .build();

        protoExemplarAdapter = new ProtoExemplarAdapter(protoExemplar);
    }


    @Test
    void get() {
        assertEquals(10, protoExemplarAdapter.getTimeUnixNanos());
        assertEquals(100, protoExemplarAdapter.getAsLong());
        assertEquals(10, protoExemplarAdapter.getSpanId());
        assertEquals(1, protoExemplarAdapter.getTraceIdHigh());
        assertEquals(2, protoExemplarAdapter.getTraceIdLow());
        assertArrayEquals(new byte[]{0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2}, protoExemplarAdapter.getTraceId());

        assertEquals(1, protoExemplarAdapter.getAttributes().getSize());
        assertEquals("value", protoExemplarAdapter.getAttributes().getAsString("key"));
    }

    @Test
    void setAndGet() {
        protoExemplarAdapter.setTimeUnixNanos(1);
        protoExemplarAdapter.setSpanId(2);
        protoExemplarAdapter.setTraceId(3, 4);

        assertEquals(1, protoExemplarAdapter.getTimeUnixNanos());
        assertEquals(2, protoExemplarAdapter.getSpanId());
        assertEquals(3, protoExemplarAdapter.getTraceIdHigh());
        assertEquals(4, protoExemplarAdapter.getTraceIdLow());

        protoExemplarAdapter.setAsLong(100);
        assertEquals(100, protoExemplarAdapter.getAsLong());

        protoExemplarAdapter.setAsDouble(1.2);
        assertEquals(1.2, protoExemplarAdapter.getAsDouble());

        assertNotNull(protoExemplarAdapter.getAttributes());

        var attributes = protoExemplarAdapter.getAttributes();
        attributes.set("new-key", "new-value");

        assertEquals(2, attributes.getSize());
        assertEquals("value", attributes.getAsString("key"));
        assertEquals("new-value", attributes.getAsString("new-key"));

    }


    @Test
    void getUpdatedExemplar_notUpdatable() {

        protoExemplarAdapter = new ProtoExemplarAdapter(protoExemplar);

        assertSame(protoExemplar, protoExemplarAdapter.getUpdated());

    }

    @Test
    void getUpdatableExemplar_nothingUpdated() {

        assertSame(protoExemplar, protoExemplarAdapter.getUpdated());

    }

    @Test
    void getUpdatedExemplar_onlyExemplarUpdated() {

        protoExemplarAdapter.setSpanId(1);
        protoExemplarAdapter.setTraceId(2, 3);
        protoExemplarAdapter.setTimeUnixNanos(4);
        protoExemplarAdapter.setAsLong(10);

        Exemplar updatedExemplar = protoExemplarAdapter.getUpdated();
        assertEquals(4, updatedExemplar.getTimeUnixNano());
        assertEquals(10, updatedExemplar.getAsInt());
        assertEquals(AdapterUtils.spanId(1),updatedExemplar.getSpanId());
        assertEquals(AdapterUtils.traceId(2, 3),updatedExemplar.getTraceId());

        assertSame(protoExemplar.getFilteredAttributesList(), updatedExemplar.getFilteredAttributesList());
    }

    @Test
    void getUpdatedExemplar_onlyAttributesChanged() {

        protoExemplarAdapter.getAttributes().set("str-attribute", "new-value");

        assertEquals(10, protoExemplarAdapter.getTimeUnixNanos());
        assertEquals(100, protoExemplarAdapter.getAsLong());
        assertEquals(10, protoExemplarAdapter.getSpanId());
        assertEquals(1, protoExemplarAdapter.getTraceIdHigh());
        assertEquals(2, protoExemplarAdapter.getTraceIdLow());
        assertArrayEquals(new byte[]{0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2}, protoExemplarAdapter.getTraceId());

        assertEquals(2, protoExemplarAdapter.getAttributes().getSize());
        assertEquals("value", protoExemplarAdapter.getAttributes().getAsString("key"));
        assertEquals("new-value", protoExemplarAdapter.getAttributes().getAsString("str-attribute"));

    }
}