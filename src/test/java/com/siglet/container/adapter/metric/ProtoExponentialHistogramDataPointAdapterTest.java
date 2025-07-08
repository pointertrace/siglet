package com.siglet.container.adapter.metric;

import com.siglet.container.adapter.AdapterUtils;
import com.siglet.container.adapter.common.ProtoAttributesAdapter;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.Exemplar;
import io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProtoExponentialHistogramDataPointAdapterTest {


    private ExponentialHistogramDataPoint exponentialHistogramDataPoint;

    private List<KeyValue> attributes;

    private List<Exemplar> exemplars;

    private ProtoExponentialHistogramDataPointAdapter protoExponentialHistogramDataPointAdapter;

    private ExponentialHistogramDataPoint.Buckets protoPositiveBuckets;

    private ExponentialHistogramDataPoint.Buckets protoNegativeBuckets;


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
                        .setSpanId(AdapterUtils.spanId(10))
                        .setTraceId(AdapterUtils.traceId(20, 30))
                        .setAsInt(40)
                        .setTimeUnixNano(50)
                        .build(),
                Exemplar.newBuilder()
                        .setSpanId(AdapterUtils.spanId(60))
                        .setTraceId(AdapterUtils.traceId(70, 80))
                        .setAsInt(90)
                        .setTimeUnixNano(100)
                        .build());

        protoPositiveBuckets = ExponentialHistogramDataPoint.Buckets.newBuilder()
                .setOffset(1)
                .addBucketCounts(10)
                .build();

        protoNegativeBuckets = ExponentialHistogramDataPoint.Buckets.newBuilder()
                .setOffset(2)
                .addBucketCounts(20)
                .build();

        exponentialHistogramDataPoint = ExponentialHistogramDataPoint.newBuilder()
                .setFlags(1)
                .setTimeUnixNano(2)
                .setStartTimeUnixNano(3)
                .setZeroThreshold(4)
                .setZeroCount(5)
                .setScale(6)
                .setCount(7)
                .setMin(11.1)
                .setMax(12.2)
                .setSum(13.3)
                .setPositive(protoPositiveBuckets)
                .setNegative(protoNegativeBuckets)
                .addAllAttributes(attributes)
                .addAllExemplars(exemplars)
                .build();

        protoExponentialHistogramDataPointAdapter = new ProtoExponentialHistogramDataPointAdapter();
        protoExponentialHistogramDataPointAdapter.recycle(exponentialHistogramDataPoint);
    }

    @Test
    void get() {
        assertEquals(1, protoExponentialHistogramDataPointAdapter.getFlags());
        assertEquals(2, protoExponentialHistogramDataPointAdapter.getTimeUnixNano());
        assertEquals(3, protoExponentialHistogramDataPointAdapter.getStartTimeUnixNano());
        assertEquals(4, protoExponentialHistogramDataPointAdapter.getZeoThreshold());
        assertEquals(5, protoExponentialHistogramDataPointAdapter.getZeroCount());
        assertEquals(6, protoExponentialHistogramDataPointAdapter.getScale());
        assertEquals(7, protoExponentialHistogramDataPointAdapter.getCount());
        assertEquals(11.1, protoExponentialHistogramDataPointAdapter.getMin());
        assertEquals(12.2, protoExponentialHistogramDataPointAdapter.getMax());
        assertEquals(13.3, protoExponentialHistogramDataPointAdapter.getSum());

        assertEquals("first-attribute-value", protoExponentialHistogramDataPointAdapter.getAttributes().getAsString(("first-attribute")));
        assertEquals("second-attribute-value", protoExponentialHistogramDataPointAdapter.getAttributes().getAsString("second-attribute"));

        assertEquals(10, protoExponentialHistogramDataPointAdapter.getExemplars().get(0).getSpanId());
        assertEquals(20, AdapterUtils.traceIdHigh(protoExponentialHistogramDataPointAdapter.getExemplars().get(0).getTraceId()));
        assertEquals(30, AdapterUtils.traceIdLow(protoExponentialHistogramDataPointAdapter.getExemplars().get(0).getTraceId()));
        assertEquals(40, protoExponentialHistogramDataPointAdapter.getExemplars().get(0).getAsLong());
        assertEquals(50, protoExponentialHistogramDataPointAdapter.getExemplars().get(0).getTimeUnixNanos());
        assertEquals(60, protoExponentialHistogramDataPointAdapter.getExemplars().get(1).getSpanId());
        assertEquals(70, AdapterUtils.traceIdHigh(protoExponentialHistogramDataPointAdapter.getExemplars().get(1).getTraceId()));
        assertEquals(80, AdapterUtils.traceIdLow(protoExponentialHistogramDataPointAdapter.getExemplars().get(1).getTraceId()));
        assertEquals(90, protoExponentialHistogramDataPointAdapter.getExemplars().get(1).getAsLong());
        assertEquals(100, protoExponentialHistogramDataPointAdapter.getExemplars().get(1).getTimeUnixNanos());

        assertEquals(1, protoExponentialHistogramDataPointAdapter.getPositive().getOffset());
        assertEquals(1, protoExponentialHistogramDataPointAdapter.getPositive().getBucketCounts().size());
        assertEquals(10, protoExponentialHistogramDataPointAdapter.getPositive().getBucketCounts().getFirst());

        assertEquals(2, protoExponentialHistogramDataPointAdapter.getNegative().getOffset());
        assertEquals(1, protoExponentialHistogramDataPointAdapter.getNegative().getBucketCounts().size());
        assertEquals(20, protoExponentialHistogramDataPointAdapter.getNegative().getBucketCounts().getFirst());

        assertFalse(protoExponentialHistogramDataPointAdapter.isUpdated());

    }

    @Test
    void setAndGet() {

        protoExponentialHistogramDataPointAdapter
                .setFlags(10)
                .setTimeUnixNano(20)
                .setStartTimeUnixNano(30)
                .setZeroThreshold(40)
                .setZeroCount(50)
                .setScale(60)
                .setCount(70)
                .setMin(111.1)
                .setMax(112.2)
                .setSum(113.3);


        assertEquals(10, protoExponentialHistogramDataPointAdapter.getFlags());
        assertEquals(20, protoExponentialHistogramDataPointAdapter.getTimeUnixNano());
        assertEquals(30, protoExponentialHistogramDataPointAdapter.getStartTimeUnixNano());
        assertEquals(40, protoExponentialHistogramDataPointAdapter.getZeoThreshold());
        assertEquals(50, protoExponentialHistogramDataPointAdapter.getZeroCount());
        assertEquals(60, protoExponentialHistogramDataPointAdapter.getScale());
        assertEquals(70, protoExponentialHistogramDataPointAdapter.getCount());
        assertEquals(111.1, protoExponentialHistogramDataPointAdapter.getMin());
        assertEquals(112.2, protoExponentialHistogramDataPointAdapter.getMax());
        assertEquals(113.3, protoExponentialHistogramDataPointAdapter.getSum());


        assertTrue(protoExponentialHistogramDataPointAdapter.isUpdated());
    }

    @Test
    void attributesChangeAndGet() {

        protoExponentialHistogramDataPointAdapter.getAttributes().set("first-attribute", "new-value");
        protoExponentialHistogramDataPointAdapter.getAttributes().set("extra-attribute", "extra-attribute-value");

        ProtoAttributesAdapter attributes = protoExponentialHistogramDataPointAdapter.getAttributes();

        assertEquals(3, attributes.getSize());
        assertEquals("new-value", attributes.getAsString("first-attribute"));
        assertEquals("extra-attribute-value", attributes.getAsString("extra-attribute"));

        assertTrue(protoExponentialHistogramDataPointAdapter.isUpdated());
    }

    @Test
    void exemplarsChangeAndGet() {

        protoExponentialHistogramDataPointAdapter.getExemplars().add()
                .setTraceId(100, 200)
                .setSpanId(100)
                .setTimeUnixNanos(1000)
                .setAsDouble(2000);

        assertEquals(3, protoExponentialHistogramDataPointAdapter.getExemplars().getSize());

        ProtoExemplarsAdapter exemplars = protoExponentialHistogramDataPointAdapter.getExemplars();
        ProtoExemplarAdapter exemplar = exemplars.get(0);


        assertEquals(10, protoExponentialHistogramDataPointAdapter.getExemplars().get(0).getSpanId());
        assertEquals(20, AdapterUtils.traceIdHigh(protoExponentialHistogramDataPointAdapter.getExemplars().get(0).getTraceId()));
        assertEquals(30, AdapterUtils.traceIdLow(protoExponentialHistogramDataPointAdapter.getExemplars().get(0).getTraceId()));
        assertEquals(40, protoExponentialHistogramDataPointAdapter.getExemplars().get(0).getAsLong());
        assertEquals(50, protoExponentialHistogramDataPointAdapter.getExemplars().get(0).getTimeUnixNanos());

        assertEquals(60, protoExponentialHistogramDataPointAdapter.getExemplars().get(1).getSpanId());
        assertEquals(70, AdapterUtils.traceIdHigh(protoExponentialHistogramDataPointAdapter.getExemplars().get(1).getTraceId()));
        assertEquals(80, AdapterUtils.traceIdLow(protoExponentialHistogramDataPointAdapter.getExemplars().get(1).getTraceId()));
        assertEquals(90, protoExponentialHistogramDataPointAdapter.getExemplars().get(1).getAsLong());
        assertEquals(100, protoExponentialHistogramDataPointAdapter.getExemplars().get(1).getTimeUnixNanos());

        assertEquals(100, protoExponentialHistogramDataPointAdapter.getExemplars().get(2).getSpanId());
        assertEquals(100, AdapterUtils.traceIdHigh(protoExponentialHistogramDataPointAdapter.getExemplars().get(2).getTraceId()));
        assertEquals(200, AdapterUtils.traceIdLow(protoExponentialHistogramDataPointAdapter.getExemplars().get(2).getTraceId()));
        assertEquals(1000, protoExponentialHistogramDataPointAdapter.getExemplars().get(2).getTimeUnixNanos());
        assertEquals(2000, protoExponentialHistogramDataPointAdapter.getExemplars().get(2).getAsDouble());
        assertTrue(protoExponentialHistogramDataPointAdapter.isUpdated());

    }

    @Test
    void positiveBucketsChangeAndGet() {

        protoExponentialHistogramDataPointAdapter.getPositive()
                .clearBucketCounts()
                .addBucketCount(1)
                .addBucketCount(2);

        ProtoBucketsAdapter positive = protoExponentialHistogramDataPointAdapter.getPositive();

        assertNotNull(positive);
        assertEquals(2, positive.getBucketCounts().size());
        assertEquals(1, positive.getBucketCounts().get(0));
        assertEquals(2, positive.getBucketCounts().get(1));

        assertTrue(protoExponentialHistogramDataPointAdapter.isUpdated());
    }

    @Test
    void negativeBucketsChangeAndGet() {

        protoExponentialHistogramDataPointAdapter.getNegative()
                .clearBucketCounts()
                .addBucketCount(1)
                .addBucketCount(2);

        ProtoBucketsAdapter negative = protoExponentialHistogramDataPointAdapter.getNegative();

        assertNotNull(negative);
        assertEquals(2, negative.getBucketCounts().size());
        assertEquals(1, negative.getBucketCounts().get(0));
        assertEquals(2, negative.getBucketCounts().get(1));

        assertTrue(protoExponentialHistogramDataPointAdapter.isUpdated());
    }

    @Test
    void getUpdated_notUpdatable() {

        protoExponentialHistogramDataPointAdapter = new ProtoExponentialHistogramDataPointAdapter();
        protoExponentialHistogramDataPointAdapter.recycle(exponentialHistogramDataPoint);

        ExponentialHistogramDataPoint histogramDataPoint = protoExponentialHistogramDataPointAdapter.getUpdated();

        assertSame(exponentialHistogramDataPoint, histogramDataPoint);

    }

    @Test
    void getUpdated_nothingUpdated() {

        ExponentialHistogramDataPoint histogramDataPoint = protoExponentialHistogramDataPointAdapter.getUpdated();

        assertSame(exponentialHistogramDataPoint, histogramDataPoint);
    }

    @Test
    void getUpdated_onlyAttributesUpdated() {

        ProtoAttributesAdapter attributes = protoExponentialHistogramDataPointAdapter.getAttributes();

        attributes.set("first-attribute", "new-first-key-value");
        attributes.set("new-key", "new-key-value");


        ExponentialHistogramDataPoint actual = protoExponentialHistogramDataPointAdapter.getUpdated();

        List<KeyValue> actualAttributes = actual.getAttributesList();

        assertEquals(3, actualAttributes.size());

        assertEquals("first-attribute", actualAttributes.get(0).getKey());
        assertEquals("new-first-key-value", actualAttributes.get(0).getValue().getStringValue());

        assertEquals("second-attribute", actualAttributes.get(1).getKey());
        assertEquals("second-attribute-value", actualAttributes.get(1).getValue().getStringValue());

        assertEquals("new-key", actualAttributes.get(2).getKey());
        assertEquals("new-key-value", actualAttributes.get(2).getValue().getStringValue());

    }

    @Test
    void getUpdated_onlyExemplarsUpdated() {

        ProtoExemplarsAdapter exemplars = protoExponentialHistogramDataPointAdapter.getExemplars();

        exemplars.remove(0);
        exemplars.add()
                .setSpanId(600)
                .setTraceId(700, 800)
                .setAsLong(900)
                .setTimeUnixNanos(1000);


        ExponentialHistogramDataPoint actual = protoExponentialHistogramDataPointAdapter.getUpdated();

        List<Exemplar> actualExemplars = actual.getExemplarsList();

        assertEquals(2, actualExemplars.size());

        assertEquals(AdapterUtils.spanId(60), actualExemplars.get(0).getSpanId());
        assertEquals(AdapterUtils.traceId(70, 80), actualExemplars.get(0).getTraceId());
        assertEquals(90, actualExemplars.get(0).getAsInt());
        assertEquals(100, actualExemplars.get(0).getTimeUnixNano());


        assertEquals(AdapterUtils.spanId(600), actualExemplars.get(1).getSpanId());
        assertEquals(AdapterUtils.traceId(700, 800), actualExemplars.get(1).getTraceId());
        assertEquals(900, actualExemplars.get(1).getAsInt());
        assertEquals(1000, actualExemplars.get(1).getTimeUnixNano());
    }
}