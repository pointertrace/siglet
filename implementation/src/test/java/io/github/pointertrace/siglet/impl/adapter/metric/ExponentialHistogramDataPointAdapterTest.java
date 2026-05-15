package io.github.pointertrace.siglet.impl.adapter.metric;

import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExponentialHistogramDataPointAdapterTest {
    @Test
    void shouldRejectNullDataPoint() {
        assertThrows(NullPointerException.class, () -> new ExponentialHistogramDataPointAdapter(null, null));
    }

    @Test
    void shouldKeepSameProtoInstanceWhenUnchanged() {
        io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint value =
            io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.getDefaultInstance();
        ExponentialHistogramDataPointAdapter adapter = new ExponentialHistogramDataPointAdapter(value, null);
        assertSame(value, adapter.getUpdated());
    }

    @Test
    void shouldReturnSameContainedListInstances() {
        io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint value =
            io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.newBuilder()
                .addAttributes(KeyValue.newBuilder().setKey("k").setValue(AnyValue.newBuilder().setStringValue("v").build()).build())
                .addExemplars(io.opentelemetry.proto.metrics.v1.Exemplar.getDefaultInstance())
                .build();
        List<KeyValue> attributes = value.getAttributesList();
        List<io.opentelemetry.proto.metrics.v1.Exemplar> exemplars = value.getExemplarsList();

        ExponentialHistogramDataPointAdapter adapter = new ExponentialHistogramDataPointAdapter(value, null);

        assertSame(value, adapter.getUpdated());
        assertSame(attributes, adapter.getUpdated().getAttributesList());
        assertSame(exemplars, adapter.getUpdated().getExemplarsList());
    }

    @Test
    void shouldReturnSameContainedPositiveBucketsProtoInstance() {
        io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets positive =
            io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets.newBuilder().setOffset(1).build();
        io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint value =
            io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.newBuilder()
                .setPositive(positive).build();
        ExponentialHistogramDataPointAdapter adapter = new ExponentialHistogramDataPointAdapter(value, null);

        assertSame(positive, adapter.getUpdated().getPositive());
    }

    @Test
    void shouldReturnSameContainedNegativeBucketsProtoInstance() {
        io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets negative =
            io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets.newBuilder().setOffset(2).build();
        io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint value =
            io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.newBuilder()
                .setNegative(negative).build();
        ExponentialHistogramDataPointAdapter adapter = new ExponentialHistogramDataPointAdapter(value, null);

        assertSame(negative, adapter.getUpdated().getNegative());
    }
}
