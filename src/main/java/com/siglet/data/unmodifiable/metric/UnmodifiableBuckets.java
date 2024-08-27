package com.siglet.data.unmodifiable.metric;

import java.util.List;

public interface UnmodifiableBuckets {

    int getOffset();

    List<Long> getBucketCounts();

}
