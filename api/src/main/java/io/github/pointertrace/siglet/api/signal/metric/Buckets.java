package io.github.pointertrace.siglet.api.signal.metric;

import java.util.List;

/**
 * Represents a set of buckets for an exponential histogram, containing offset and bucket counts.
 */
public interface Buckets {

    /**
     * Returns the offset of the first bucket in the collection.
     *
     * @return bucket offset.
     */
    int getOffset();

    /**
     * Sets the offset of the first bucket in the collection.
     *
     * @param offset bucket offset.
     * @return current instance of {@code Buckets}.
     */
    Buckets setOffset(int offset);

    /**
     * Returns the list of bucket counts.
     *
     * @return list of bucket counts.
     */
    List<Long> getBucketCounts();

    /**
     * Adds a bucket count to the collection.
     *
     * @param bucketCount bucket count to add.
     * @return current instance of {@code Buckets}.
     */
    Buckets addBucketCount(long bucketCount);

    /**
     * Clears all bucket counts from this collection.
     *
     * @return current instance of {@code Buckets}.
     */
    Buckets clearBucketCounts();

}
