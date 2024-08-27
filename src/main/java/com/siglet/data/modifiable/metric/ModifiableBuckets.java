package com.siglet.data.modifiable.metric;

import com.siglet.data.unmodifiable.metric.UnmodifiableBuckets;

public interface ModifiableBuckets extends UnmodifiableBuckets {

    ModifiableBuckets setOffset(int offset);

    ModifiableBuckets addBucketCount(long bucketCount);

    ModifiableBuckets clearBucketCounts();

}
