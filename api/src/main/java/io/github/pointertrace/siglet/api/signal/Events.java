package io.github.pointertrace.siglet.api.signal;

/**
 * Defines the event list
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
     * @return { @Link Event }
     * @throws IndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index >= size()}).
     */
    Event get(int i);

    /**
     * Removes a specific event in the event list by its index.
     *
     * @param i event index to be removed.
     * @throws IndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index >= size()}).
     */
    void remove(int i);

    /**
     * Adds a new empty event to the event list.
     *
     * @return The newly created empty event.
     */
    Event add();
}
