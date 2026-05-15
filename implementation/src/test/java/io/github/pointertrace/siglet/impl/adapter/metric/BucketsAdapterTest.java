package io.github.pointertrace.siglet.impl.adapter.metric;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BucketsAdapterTest {
    @Test
    void shouldRejectNullBuckets() {
        assertThrows(NullPointerException.class, () -> new BucketsAdapter(null, null));
    }

    @Test
    void shouldKeepSameProtoInstanceWhenUnchanged() {
        io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets buckets =
            io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets.getDefaultInstance();
        BucketsAdapter adapter = new BucketsAdapter(buckets, null);
        assertSame(buckets, adapter.getUpdated());
    }

    @Test
    void shouldReturnSameContainedListInstances() {
        io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets buckets =
            io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets.newBuilder()
                .addBucketCounts(1L).addBucketCounts(2L)
                .build();
        List<Long> bucketCounts = buckets.getBucketCountsList();

        BucketsAdapter adapter = new BucketsAdapter(buckets, null);

        assertSame(buckets, adapter.getUpdated());
        assertSame(bucketCounts, adapter.getUpdated().getBucketCountsList());
    }
}
