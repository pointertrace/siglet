package io.github.pointertrace.siglet.api.signal.metric;

/**
 * Represents a collection of {@link ExponentialHistogramDataPoint} elements belonging to an exponential histogram metric.
 */
public interface ExponentialHistogramDataPoints {

    /**
     * Returns the number of data points in this collection.
     *
     * @return number of data points.
     */
    int getSize();

    /**
     * Gets a specific data point by index.
     *
     * @param i element index in the collection.
     * @return {@link ExponentialHistogramDataPoint}.
     * @throws IndexOutOfBoundsException if the index is out of range ({@code i < 0 || i >= getSize()}).
     */
    ExponentialHistogramDataPoint get(int i);

    /**
     * Removes a specific data point by index.
     *
     * @param i element index to be removed.
     * @throws IndexOutOfBoundsException if the index is out of range ({@code i < 0 || i >= getSize()}).
     */
    void remove(int i);
}
