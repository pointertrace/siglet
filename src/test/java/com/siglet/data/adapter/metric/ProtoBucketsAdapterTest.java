package com.siglet.data.adapter.metric;

import com.siglet.SigletError;
import io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProtoBucketsAdapterTest {

    private ExponentialHistogramDataPoint.Buckets protoBuckets;

    private ProtoBucketsAdapter protoBucketsAdapter;

    @BeforeEach
    void setUp() {

        protoBuckets = ExponentialHistogramDataPoint.Buckets.newBuilder()
                .setOffset(1)
                .addBucketCounts(10)
                .build();

        protoBucketsAdapter = new ProtoBucketsAdapter(protoBuckets, true);
    }

    @Test
    void get() {

        assertEquals(1, protoBucketsAdapter.getOffset());
        assertEquals(1, protoBucketsAdapter.getBucketCounts().size());
        assertEquals(10, protoBucketsAdapter.getBucketCounts().get(0));
        assertFalse(protoBucketsAdapter.isUpdated());

    }

    @Test
    void setAndGet() {

        protoBucketsAdapter
                .setOffset(10)
                .addBucketCount(20);

        assertEquals(10, protoBucketsAdapter.getOffset());
        assertEquals(2, protoBucketsAdapter.getBucketCounts().size());
        assertEquals(10, protoBucketsAdapter.getBucketCounts().get(0));
        assertEquals(20, protoBucketsAdapter.getBucketCounts().get(1));
        assertTrue(protoBucketsAdapter.isUpdated());

    }

    @Test
    void changeNonUpdatable() {

        protoBucketsAdapter = new ProtoBucketsAdapter(protoBuckets, false);

        assertThrowsExactly(SigletError.class, () -> {
            protoBucketsAdapter.setOffset(1);
        });

        assertThrowsExactly(SigletError.class, () -> {
            protoBucketsAdapter.addBucketCount(10);
        });

        assertThrowsExactly(SigletError.class, () -> {
            protoBucketsAdapter.clearBucketCounts();
        });

    }
}