package io.github.pointertrace.siglet.api.signal.metric;

/**
 * Represents a collection of {@link Exemplar} elements associated with a metric data point.
 */
public interface Exemplars {

    /**
     * Returns the number of exemplars in this collection.
     *
     * @return number of exemplars.
     */
    int getSize();

    /**
     * Gets a specific exemplar by index.
     *
     * @param i element index in the collection.
     * @return {@link Exemplar}.
     * @throws IndexOutOfBoundsException if the index is out of range ({@code i < 0 || i >= getSize()}).
     */
    Exemplar get(int i);

    /**
     * Removes a specific exemplar by index.
     *
     * @param i element index to be removed.
     * @throws IndexOutOfBoundsException if the index is out of range ({@code i < 0 || i >= getSize()}).
     */
    void remove(int i);

}
