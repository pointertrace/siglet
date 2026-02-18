package io.github.pointertrace.siglet.api.signal;

/**
 * Represents a collection of {@link Event} elements associated with a span.
 */
public interface Events {

    /**
     * Returns the number of events.
     *
     * @return number of events.
     */
    int getSize();

    /**
     * Gets a specific event in the event list by its index.
     *
     * @param i event index in the event list.
     * @return {@link Event}.
     * @throws IndexOutOfBoundsException if the index is out of range
     *         ({@code i < 0 || i >= getSize()}).
     */
    Event get(int i);

    /**
     * Removes a specific event in the event list by its index.
     *
     * @param i event index to be removed.
     * @throws IndexOutOfBoundsException if the index is out of range
     *         ({@code i < 0 || i >= getSize()}).
     */
    void remove(int i);

    /**
     * Adds a new empty event to the event list.
     *
     * @return newly created {@link Event}.
     */
    Event add();
}
