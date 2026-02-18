package io.github.pointertrace.siglet.api.signal.metric;

/**
 * Represents a collection of {@link HistogramDataPoint} elements belonging to a histogram metric.
 */
public interface HistogramDataPoints {

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
     * @return {@link HistogramDataPoint}.
     * @throws IndexOutOfBoundsException if the index is out of range ({@code i < 0 || i >= getSize()}).
     */
    HistogramDataPoint get(int i);

    /**
     * Removes a specific data point by index.
     *
     * @param i element index to be removed.
     * @throws IndexOutOfBoundsException if the index is out of range ({@code i < 0 || i >= getSize()}).
     */
    void remove(int i);
}
