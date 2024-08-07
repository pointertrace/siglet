package com.siglet.data.adapter;

import com.google.protobuf.ByteString;
import com.siglet.SigletError;
import com.siglet.data.adapter.metric.ProtoExemplarAdapter;
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
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(10)))
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(1, 2)))
                .addAllFilteredAttributes(List.of(KeyValue.newBuilder()
                        .setKey("key")
                        .setValue(AnyValue.newBuilder().setStringValue("value").build())
                        .build()))
                .build();

        protoExemplarAdapter = new ProtoExemplarAdapter(protoExemplar, true);
    }


    @Test
    public void get() {
        assertEquals(protoExemplarAdapter.getTimeUnixNanos(), 10);
        assertEquals(protoExemplarAdapter.getAsLong(), 100);
        assertEquals(protoExemplarAdapter.getSpanId(), 10);
        assertEquals(protoExemplarAdapter.getTraceIdHigh(), 1);
        assertEquals(protoExemplarAdapter.getTraceIdLow(), 2);
        assertArrayEquals(protoExemplarAdapter.getTraceId(), new byte[]{0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2});

        assertEquals(1, protoExemplarAdapter.getAttributes().getSize());
        assertEquals("value", protoExemplarAdapter.getAttributes().getAsString("key"));
    }

    @Test
    public void setAndGet() {
        protoExemplarAdapter.setTimeUnixNanos(1);
        protoExemplarAdapter.setSpanId(2);
        protoExemplarAdapter.setTraceId(3, 4);

        assertEquals(protoExemplarAdapter.getTimeUnixNanos(), 1);
        assertEquals(protoExemplarAdapter.getSpanId(), 2);
        assertEquals(protoExemplarAdapter.getTraceIdHigh(), 3);
        assertEquals(protoExemplarAdapter.getTraceIdLow(), 4);

        protoExemplarAdapter.setAsLong(100);
        assertEquals(protoExemplarAdapter.getAsLong(), 100);

        protoExemplarAdapter.setAsDouble(1.2);
        assertEquals(protoExemplarAdapter.getAsDouble(), 1.2);

        assertNotNull(protoExemplarAdapter.getAttributes());

        var attributes = protoExemplarAdapter.getAttributes();
        attributes.set("new-key", "new-value");

        assertEquals(2, attributes.getSize());
        assertEquals("value", attributes.getAsString("key"));
        assertEquals("new-value", attributes.getAsString("new-key"));

    }


    @Test
    public void setNotUpdatable() {
        protoExemplarAdapter = new ProtoExemplarAdapter(Exemplar.newBuilder().build(), false);

        assertThrowsExactly(SigletError.class, () -> protoExemplarAdapter.setTimeUnixNanos(1));
        assertThrowsExactly(SigletError.class, () -> protoExemplarAdapter.setSpanId(1));
        assertThrowsExactly(SigletError.class, () -> protoExemplarAdapter.setTraceId(1, 2));
        assertThrowsExactly(SigletError.class, () -> protoExemplarAdapter.setAsLong(1));
        assertThrowsExactly(SigletError.class, () -> protoExemplarAdapter.setAsDouble(1.1));
        assertThrowsExactly(SigletError.class, () -> {
            protoExemplarAdapter.getAttributes().remove("any-key");
        });

    }

    @Test
    public void getUpdatedExemplar_notUpdatable() {

        protoExemplarAdapter = new ProtoExemplarAdapter(protoExemplar, false);

        assertSame(protoExemplar, protoExemplarAdapter.getUpdatedExemplar());

    }

    @Test
    public void getUpdatableExemplar_nothingUpdated() {

        assertSame(protoExemplar, protoExemplarAdapter.getUpdatedExemplar());

    }

    @Test
    public void getUpdatedExemplar_onlyExemplarUpdated() {

        protoExemplarAdapter.setSpanId(1);
        protoExemplarAdapter.setTraceId(2, 3);
        protoExemplarAdapter.setTimeUnixNanos(4);
        protoExemplarAdapter.setAsLong(10);

        Exemplar updatedExemplar = protoExemplarAdapter.getUpdatedExemplar();
        assertEquals(updatedExemplar.getTimeUnixNano(), 4);
        assertEquals(updatedExemplar.getAsInt(), 10);
        assertEquals(updatedExemplar.getSpanId(), ByteString.copyFrom(AdapterUtils.spanId(1)));
        assertEquals(updatedExemplar.getTraceId(), ByteString.copyFrom(AdapterUtils.traceId(2, 3)));

        assertSame(protoExemplar.getFilteredAttributesList(), updatedExemplar.getFilteredAttributesList());
    }

    @Test
    public void getUpdatedExemplar_onlyAttributesChanged() {

        protoExemplarAdapter.getAttributes().set("str-attribute", "new-value");

        assertEquals(protoExemplarAdapter.getTimeUnixNanos(), 10);
        assertEquals(protoExemplarAdapter.getAsLong(), 100);
        assertEquals(protoExemplarAdapter.getSpanId(), 10);
        assertEquals(protoExemplarAdapter.getTraceIdHigh(), 1);
        assertEquals(protoExemplarAdapter.getTraceIdLow(), 2);
        assertArrayEquals(protoExemplarAdapter.getTraceId(), new byte[]{0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2});

        assertEquals(2, protoExemplarAdapter.getAttributes().getSize());
        assertEquals("value", protoExemplarAdapter.getAttributes().getAsString("key"));
        assertEquals("new-value", protoExemplarAdapter.getAttributes().getAsString("str-attribute"));

    }
}