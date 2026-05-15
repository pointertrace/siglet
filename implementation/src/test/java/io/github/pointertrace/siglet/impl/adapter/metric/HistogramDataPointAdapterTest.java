package io.github.pointertrace.siglet.impl.adapter.metric;

import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistogramDataPointAdapterTest {
    @Test
    void shouldUpdateBucketsAndBounds() {
        io.opentelemetry.proto.metrics.v1.HistogramDataPoint value = io.opentelemetry.proto.metrics.v1.HistogramDataPoint.getDefaultInstance();
        HistogramDataPointAdapter adapter = new HistogramDataPointAdapter(value, null);
        assertSame(value, adapter.getUpdated());
        adapter.setCount(3).addBucketCount(1).addBucketCount(2).addExplicitBound(10.0).setMin(1.0).setMax(9.0);
        io.opentelemetry.proto.metrics.v1.HistogramDataPoint updated = adapter.getUpdated();
        assertEquals(3L, updated.getCount());
        assertEquals(2, updated.getBucketCountsCount());
        assertEquals(1, updated.getExplicitBoundsCount());
        assertEquals(1.0, updated.getMin());
        assertEquals(9.0, updated.getMax());
    }

    @Test
    void shouldReturnSameContainedListInstances() {
        io.opentelemetry.proto.metrics.v1.HistogramDataPoint value = io.opentelemetry.proto.metrics.v1.HistogramDataPoint.newBuilder()
            .addAttributes(KeyValue.newBuilder().setKey("k").setValue(AnyValue.newBuilder().setStringValue("v").build()).build())
            .addExemplars(io.opentelemetry.proto.metrics.v1.Exemplar.getDefaultInstance())
            .addBucketCounts(5L)
            .addExplicitBounds(10.0)
            .build();
        List<KeyValue> attributes = value.getAttributesList();
        List<io.opentelemetry.proto.metrics.v1.Exemplar> exemplars = value.getExemplarsList();
        List<Long> bucketCounts = value.getBucketCountsList();
        List<Double> explicitBounds = value.getExplicitBoundsList();

        HistogramDataPointAdapter adapter = new HistogramDataPointAdapter(value, null);

        assertSame(value, adapter.getUpdated());
        assertSame(attributes, adapter.getUpdated().getAttributesList());
        assertSame(exemplars, adapter.getUpdated().getExemplarsList());
        assertSame(bucketCounts, adapter.getUpdated().getBucketCountsList());
        assertSame(explicitBounds, adapter.getUpdated().getExplicitBoundsList());
    }

    @Test
    void shouldRejectNullDataPoint() {
        assertThrows(NullPointerException.class, () -> new HistogramDataPointAdapter(null, null));
    }
}
