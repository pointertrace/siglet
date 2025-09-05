package io.github.pointertrace.siglet.api.signal.metric;

import java.util.List;

public interface Buckets {

    int getOffset();

    Buckets setOffset(int offset);

    List<Long> getBucketCounts();

    Buckets addBucketCount(long bucketCount);

    Buckets clearBucketCounts();

}
