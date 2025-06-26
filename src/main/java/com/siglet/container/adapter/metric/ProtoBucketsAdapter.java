package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableBuckets;
import com.siglet.container.adapter.Adapter;
import io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint;

import java.util.ArrayList;
import java.util.List;

public class ProtoBucketsAdapter extends Adapter<ExponentialHistogramDataPoint.Buckets,
        ExponentialHistogramDataPoint.Buckets.Builder> implements ModifiableBuckets {


    public ProtoBucketsAdapter(ExponentialHistogramDataPoint.Buckets protoExemplar) {
        super(protoExemplar, ExponentialHistogramDataPoint.Buckets::toBuilder,
                ExponentialHistogramDataPoint.Buckets.Builder::build);
    }

    public ProtoBucketsAdapter(ExponentialHistogramDataPoint.Buckets.Builder bucketsBuilder) {
        super(bucketsBuilder, ExponentialHistogramDataPoint.Buckets.Builder::build);

    }

    @Override
    public ModifiableBuckets setOffset(int offset) {
        setValue(ExponentialHistogramDataPoint.Buckets.Builder::setOffset, offset);
        return this;
    }

    @Override
    public ModifiableBuckets addBucketCount(long bucketCount) {
        prepareUpdate();
        getBuilder().addBucketCounts(bucketCount);
        return this;
    }

    @Override
    public ModifiableBuckets clearBucketCounts() {
        prepareUpdate();
        getBuilder().clearBucketCounts();
        return this;
    }

    @Override
    public int getOffset() {
        return getValue(ExponentialHistogramDataPoint.Buckets::getOffset,
                ExponentialHistogramDataPoint.Buckets.Builder::getOffset);
    }

    @Override
    public List<Long> getBucketCounts() {
        return new ArrayList(getValue(ExponentialHistogramDataPoint.Buckets::getBucketCountsList,
                ExponentialHistogramDataPoint.Buckets.Builder::getBucketCountsList));
    }
}
