package com.siglet.data.adapter;

import com.google.protobuf.ByteString;
import com.siglet.data.adapter.metric.ProtoDataPointsAdapter;
import com.siglet.data.adapter.metric.ProtoExemplarAdapter;
import com.siglet.data.adapter.metric.ProtoNumberDataPointAdapter;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.Exemplar;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProtoNumberDataPointAdapterTest {

    private NumberDataPoint protoNumberDataPoint;

    private ProtoNumberDataPointAdapter protoNumberDataPointAdapter;

    @BeforeEach
    void setUp() {
        protoNumberDataPoint = NumberDataPoint.newBuilder()
                .setFlags(1)
                .setTimeUnixNano(1)
                .setStartTimeUnixNano(2)
                .setAsInt(3)
                .addAttributes(KeyValue.newBuilder()
                        .setKey("first-attribute")
                        .setValue(AnyValue.newBuilder().setStringValue("first-attribute-value").build())
                        .build())
                .addAttributes(KeyValue.newBuilder()
                        .setKey("second-attribute")
                        .setValue(AnyValue.newBuilder().setStringValue("second-attribute-value").build())
                        .build())
                .addExemplars(Exemplar.newBuilder()
                        .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(10)))
                        .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(20, 30)))
                        .setAsInt(40)
                        .setTimeUnixNano(50)
                        .build())
                .addExemplars(Exemplar.newBuilder()
                        .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(60)))
                        .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(70, 80)))
                        .setAsInt(90)
                        .setTimeUnixNano(100)
                        .build())
                .build();

        protoNumberDataPointAdapter = new ProtoNumberDataPointAdapter(protoNumberDataPoint, true);
    }

    @Test
    public void get() {
        assertEquals(1, protoNumberDataPointAdapter.getFlags());
        assertEquals(1, protoNumberDataPointAdapter.getTimeUnixNano());
        assertEquals(2, protoNumberDataPointAdapter.getStartTimeUnixNano());
        assertEquals(3, protoNumberDataPointAdapter.getAsLong());

        assertEquals("first-attribute-value", protoNumberDataPointAdapter.getAttributes().getAsString(("first-attribute")));
        assertEquals("second-attribute-value", protoNumberDataPointAdapter.getAttributes().getAsString("second-attribute"));

        assertEquals(10, protoNumberDataPointAdapter.getExemplars().get(0).getSpanId());
        assertEquals(20, AdapterUtils.traceIdHigh(protoNumberDataPointAdapter.getExemplars().get(0).getTraceId()));
        assertEquals(30, AdapterUtils.traceIdLow(protoNumberDataPointAdapter.getExemplars().get(0).getTraceId()));
        assertEquals(40, protoNumberDataPointAdapter.getExemplars().get(0).getAsLong());
        assertEquals(50, protoNumberDataPointAdapter.getExemplars().get(0).getTimeUnixNanos());
        assertEquals(60, protoNumberDataPointAdapter.getExemplars().get(1).getSpanId());
        assertEquals(70, AdapterUtils.traceIdHigh(protoNumberDataPointAdapter.getExemplars().get(1).getTraceId()));
        assertEquals(80, AdapterUtils.traceIdLow(protoNumberDataPointAdapter.getExemplars().get(1).getTraceId()));
        assertEquals(90, protoNumberDataPointAdapter.getExemplars().get(1).getAsLong());
        assertEquals(100, protoNumberDataPointAdapter.getExemplars().get(1).getTimeUnixNanos());

        assertFalse(protoNumberDataPointAdapter.isUpdated());

    }

    @Test
    public void setAndGet() {

        protoNumberDataPointAdapter.setFlags(100);
        protoNumberDataPointAdapter.setTimeUnixNano(200);
        protoNumberDataPointAdapter.setStartTimeUnixNano(300);
        protoNumberDataPointAdapter.setAsLong(400);

        assertEquals(100, protoNumberDataPointAdapter.getFlags());
        assertEquals(200, protoNumberDataPointAdapter.getTimeUnixNano());
        assertEquals(300, protoNumberDataPointAdapter.getStartTimeUnixNano());
        assertEquals(400, protoNumberDataPointAdapter.getAsLong());

        assertTrue(protoNumberDataPointAdapter.isUpdated());

    }

    @Test
    public void attributesChangeAndGet() {

        protoNumberDataPointAdapter.getAttributes().set("first-attribute", "new-value");
        protoNumberDataPointAdapter.getAttributes().set("extra-attribute", "extra-attribute-value");

        ProtoAttributesAdapter attributes = protoNumberDataPointAdapter.getAttributes();

        assertEquals(3, attributes.size());
        assertEquals("new-value", attributes.getAsString("first-attribute"));
        assertEquals("extra-attribute-value", attributes.getAsString("extra-attribute"));

        assertTrue(protoNumberDataPointAdapter.isUpdated());
    }

    @Test
    public void exemplarsChangeAndGet() {

        protoNumberDataPointAdapter.getExemplars().add(
                new ProtoExemplarAdapter(Exemplar.newBuilder()
                        .setTimeUnixNano(1000)
                        .setAsDouble(2000)
                        .build(), true));

        assertEquals(3, protoNumberDataPointAdapter.getExemplars().getSize());

        assertEquals(10, protoNumberDataPointAdapter.getExemplars().get(0).getSpanId());
        assertEquals(20, AdapterUtils.traceIdHigh(protoNumberDataPointAdapter.getExemplars().get(0).getTraceId()));
        assertEquals(30, AdapterUtils.traceIdLow(protoNumberDataPointAdapter.getExemplars().get(0).getTraceId()));
        assertEquals(40, protoNumberDataPointAdapter.getExemplars().get(0).getAsLong());
        assertEquals(50, protoNumberDataPointAdapter.getExemplars().get(0).getTimeUnixNanos());

        assertEquals(60, protoNumberDataPointAdapter.getExemplars().get(1).getSpanId());
        assertEquals(70, AdapterUtils.traceIdHigh(protoNumberDataPointAdapter.getExemplars().get(1).getTraceId()));
        assertEquals(80, AdapterUtils.traceIdLow(protoNumberDataPointAdapter.getExemplars().get(1).getTraceId()));
        assertEquals(90, protoNumberDataPointAdapter.getExemplars().get(1).getAsLong());
        assertEquals(100, protoNumberDataPointAdapter.getExemplars().get(1).getTimeUnixNanos());

        assertEquals(1000, protoNumberDataPointAdapter.getExemplars().get(2).getTimeUnixNanos());
        assertEquals(2000, protoNumberDataPointAdapter.getExemplars().get(2).getAsDouble());
        assertTrue(protoNumberDataPointAdapter.isUpdated());

    }

    @Test
    public void getUpdated_notUpdatable() {

        protoNumberDataPointAdapter = new ProtoNumberDataPointAdapter(protoNumberDataPoint, false);

        NumberDataPoint numberDataPoint = protoNumberDataPointAdapter.getUpdatedNumberDataPointAdapter();

        assertSame(protoNumberDataPoint, numberDataPoint);

    }

    @Test
    public void getUpdated_nothingUpdated() {

        NumberDataPoint numberDataPoint = protoNumberDataPointAdapter.getUpdatedNumberDataPointAdapter();

        assertSame(protoNumberDataPoint, numberDataPoint);
    }

}