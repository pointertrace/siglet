package io.github.pointertrace.siglet.container.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.Buckets;
import io.github.pointertrace.siglet.container.adapter.Adapter;
import io.github.pointertrace.siglet.container.adapter.AdapterConfig;
import io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint;

import java.util.ArrayList;
import java.util.List;

public class ProtoBucketsAdapter extends Adapter<ExponentialHistogramDataPoint.Buckets,
        ExponentialHistogramDataPoint.Buckets.Builder> implements Buckets {


    public ProtoBucketsAdapter() {
        super(AdapterConfig.BUCKETS_ADAPTER_CONFIG);
    }

    @Override
    public Buckets setOffset(int offset) {
        setValue(ExponentialHistogramDataPoint.Buckets.Builder::setOffset, offset);
        return this;
    }

    @Override
    public Buckets addBucketCount(long bucketCount) {
        prepareUpdate();
        getBuilder().addBucketCounts(bucketCount);
        return this;
    }

    @Override
    public Buckets clearBucketCounts() {
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
        return new ArrayList<>(getValue(ExponentialHistogramDataPoint.Buckets::getBucketCountsList,
                ExponentialHistogramDataPoint.Buckets.Builder::getBucketCountsList));
    }
}
