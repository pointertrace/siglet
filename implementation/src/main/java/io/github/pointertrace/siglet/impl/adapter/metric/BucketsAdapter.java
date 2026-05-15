package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.Buckets;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class BucketsAdapter implements Buckets {
    private final io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets original;
    private io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets.Builder builder;
    private final Consumer<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets> onChange;

    public BucketsAdapter(io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets value) {
        this(value, null);
    }

    public BucketsAdapter(io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets value,
                          Consumer<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets> onChange) {
        this.original = Objects.requireNonNull(value, "value");
        this.onChange = onChange;
    }

    private io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets.Builder mutate() {
        if (builder == null) {
            builder = original.toBuilder();
        }
        return builder;
    }

    private io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.BucketsOrBuilder view() {
        return builder == null ? original : builder;
    }

    public io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets getUpdated() {
        return builder == null ? original : builder.build();
    }


    private void changed() {
        if (onChange != null) {
            onChange.accept(getUpdated());
        }
    }

    @Override
    public int getOffset() { return view().getOffset(); }

    @Override
    public BucketsAdapter setOffset(int offset) {
        mutate().setOffset(offset);
        changed();
        return this;
    }

    @Override
    public List<Long> getBucketCounts() { return new ArrayList<>(view().getBucketCountsList()); }

    @Override
    public BucketsAdapter addBucketCount(long bucketCount) {
        mutate().addBucketCounts(bucketCount);
        changed();
        return this;
    }

    @Override
    public BucketsAdapter clearBucketCounts() {
        mutate().clearBucketCounts();
        changed();
        return this;
    }
}
