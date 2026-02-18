package io.github.pointertrace.siglet.api.signal.metric;

/**
 * Represents a collection of {@link ValueAtQuantile} elements associated with a metric signal.
 */
public interface ValueAtQuantiles {

    /**
     * Returns the number of value at quantile elements in this collection.
     *
     * @return number of value at quantile elements.
     */
    int getSize();

    /**
     * Gets a specific value at quantile element by its index.
     *
     * @param i element index in the collection.
     * @return {@link ValueAtQuantile}
     * @throws IndexOutOfBoundsException if the index is out of range
     *         ({@code i < 0 || i >= getSize()}).
     */
    ValueAtQuantile get(int i);

    /**
     * Removes a specific value at quantile element by its index.
     *
     * @param i element index to be removed.
     * @throws IndexOutOfBoundsException if the index is out of range
     *         ({@code i < 0 || i >= getSize()}).
     */
    void remove(int i);
}
