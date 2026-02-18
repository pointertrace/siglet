package io.github.pointertrace.siglet.api.signal.metric;

/**
 * Represents a collection of {@link SummaryDataPoint} elements belonging to a summary metric.
 */
public interface SummaryDataPoints {

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
     * @return {@link SummaryDataPoint}.
     * @throws IndexOutOfBoundsException if the index is out of range ({@code i < 0 || i >= getSize()}).
     */
    SummaryDataPoint get(int i);

    /**
     * Removes a specific data point by index.
     *
     * @param i element index to be removed.
     * @throws IndexOutOfBoundsException if the index is out of range ({@code i < 0 || i >= getSize()}).
     */
    void remove(int i);

    /**
     * Adds a new data point to the collection.
     *
     * @return newly created {@link SummaryDataPoint}.
     */
    SummaryDataPoint add();
}
