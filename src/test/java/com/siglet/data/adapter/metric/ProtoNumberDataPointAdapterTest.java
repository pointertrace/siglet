package com.siglet.data.adapter.metric;

import com.google.protobuf.ByteString;
import com.siglet.data.adapter.AdapterUtils;
import com.siglet.data.adapter.common.ProtoAttributesAdapter;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.Exemplar;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProtoNumberDataPointAdapterTest {

    private NumberDataPoint protoNumberDataPoint;

    private List<KeyValue> attributes;

    private List<Exemplar> exemplars;

    private ProtoNumberDataPointAdapter protoNumberDataPointAdapter;

    @BeforeEach
    void setUp() {

        attributes = List.of(
                KeyValue.newBuilder()
                        .setKey("first-attribute")
                        .setValue(AnyValue.newBuilder().setStringValue("first-attribute-value").build())
                        .build(),
                KeyValue.newBuilder()
                        .setKey("second-attribute")
                        .setValue(AnyValue.newBuilder().setStringValue("second-attribute-value").build())
                        .build());

        exemplars = List.of(
                Exemplar.newBuilder()
                        .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(10)))
                        .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(20, 30)))
                        .setAsInt(40)
                        .setTimeUnixNano(50)
                        .build(),
                Exemplar.newBuilder()
                        .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(60)))
                        .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(70, 80)))
                        .setAsInt(90)
                        .setTimeUnixNano(100)
                        .build());

        protoNumberDataPoint = NumberDataPoint.newBuilder()
                .setFlags(1)
                .setTimeUnixNano(1)
                .setStartTimeUnixNano(2)
                .setAsInt(3)
                .addAllAttributes(attributes)
                .addAllExemplars(exemplars)
                .build();

        protoNumberDataPointAdapter = new ProtoNumberDataPointAdapter(protoNumberDataPoint, true);
    }

    @Test
    void get() {
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
    void setAndGet() {

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
    void attributesChangeAndGet() {

        protoNumberDataPointAdapter.getAttributes().set("first-attribute", "new-value");
        protoNumberDataPointAdapter.getAttributes().set("extra-attribute", "extra-attribute-value");

        ProtoAttributesAdapter attributes = protoNumberDataPointAdapter.getAttributes();

        assertEquals(3, attributes.getSize());
        assertEquals("new-value", attributes.getAsString("first-attribute"));
        assertEquals("extra-attribute-value", attributes.getAsString("extra-attribute"));

        assertTrue(protoNumberDataPointAdapter.isUpdated());
    }

    @Test
    void exemplarsChangeAndGet() {

        protoNumberDataPointAdapter.getExemplars().add()
                .setTraceId(100, 200)
                .setSpanId(100)
                .setTimeUnixNanos(1000)
                .setAsDouble(2000);

        assertEquals(3, protoNumberDataPointAdapter.getExemplars().getSize());

        ProtoExemplarsAdapter exemplars = protoNumberDataPointAdapter.getExemplars();
        ProtoExemplarAdapter exemplar = exemplars.get(0);


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

        assertEquals(100, protoNumberDataPointAdapter.getExemplars().get(2).getSpanId());
        assertEquals(100, AdapterUtils.traceIdHigh(protoNumberDataPointAdapter.getExemplars().get(2).getTraceId()));
        assertEquals(200, AdapterUtils.traceIdLow(protoNumberDataPointAdapter.getExemplars().get(2).getTraceId()));
        assertEquals(1000, protoNumberDataPointAdapter.getExemplars().get(2).getTimeUnixNanos());
        assertEquals(2000, protoNumberDataPointAdapter.getExemplars().get(2).getAsDouble());
        assertTrue(protoNumberDataPointAdapter.isUpdated());

    }

    @Test
    void getUpdated_notUpdatable() {

        protoNumberDataPointAdapter = new ProtoNumberDataPointAdapter(protoNumberDataPoint, false);

        NumberDataPoint numberDataPoint = protoNumberDataPointAdapter.getUpdated();

        assertSame(protoNumberDataPoint, numberDataPoint);

    }

    @Test
    void getUpdated_nothingUpdated() {

        NumberDataPoint numberDataPoint = protoNumberDataPointAdapter.getUpdated();

        assertSame(protoNumberDataPoint, numberDataPoint);
    }

    @Test
    void getUpdated_onlyNumberDataPointAdapterUpdated() {

        protoNumberDataPointAdapter.setTimeUnixNano(2500);
        protoNumberDataPointAdapter.setStartTimeUnixNano(3500);
        protoNumberDataPointAdapter.setFlags(100);
        protoNumberDataPointAdapter.setAsDouble(1.1);

        assertEquals(2500, protoNumberDataPointAdapter.getTimeUnixNano());
        assertEquals(3500, protoNumberDataPointAdapter.getStartTimeUnixNano());
        assertEquals(100, protoNumberDataPointAdapter.getFlags());
        assertEquals(1.1, protoNumberDataPointAdapter.getAsDouble());

        List<KeyValue> actualAttributes = protoNumberDataPointAdapter.getAttributes().getUpdated();

        assertEquals(2, actualAttributes.size());

        assertSame(actualAttributes.get(0), attributes.get(0));
        assertSame(actualAttributes.get(1), attributes.get(1));

        List<Exemplar> actualExemplars = protoNumberDataPointAdapter.getExemplars().getUpdated();

        assertEquals(2, actualExemplars.size());

        assertSame(actualExemplars.get(0), exemplars.get(0));
        assertSame(actualExemplars.get(1), exemplars.get(1));


    }

    @Test
    void getUpdated_onlyAttributesUpdated() {

        ProtoAttributesAdapter attributes = protoNumberDataPointAdapter.getAttributes();

        attributes.set("first-attribute", "new-first-key-value");
        attributes.set("new-key", "new-key-value");


        NumberDataPoint actual = protoNumberDataPointAdapter.getUpdated();

        assertEquals(1, actual.getFlags());
        assertEquals(1, actual.getTimeUnixNano());
        assertEquals(2, actual.getStartTimeUnixNano());
        assertEquals(3, actual.getAsInt());

        List<Exemplar> actualExemplars = actual.getExemplarsList();

        assertEquals(2, actualExemplars.size());

        assertSame(actualExemplars.get(0), exemplars.get(0));
        assertSame(actualExemplars.get(1), exemplars.get(1));

        List<KeyValue> actualAttributes = actual.getAttributesList();

        assertEquals(3, actualAttributes.size());

        assertEquals("first-attribute", actualAttributes.get(0).getKey());
        assertEquals("new-first-key-value", actualAttributes.get(0).getValue().getStringValue());

        assertEquals("second-attribute", actualAttributes.get(1).getKey());
        assertEquals("second-attribute-value", actualAttributes.get(1).getValue().getStringValue());

        assertEquals("new-key", actualAttributes.get(2).getKey());
        assertEquals("new-key-value", actualAttributes.get(2).getValue().getStringValue());

    }
}