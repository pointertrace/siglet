package io.github.pointertrace.siglet.api.signal.trace;

import java.util.Map;

/**
 * Represents a collection of {@link Link} elements associated with a span.
 */
public interface Links {

    /**
     * Returns the number of links in this collection.
     *
     * @return number of links.
     */
    int getSize();

    /**
     * Checks if a link exists with the specified trace ID and span ID.
     *
     * @param traceIdHigh high 64 bits of trace ID.
     * @param traceIdLow low 64 bits of trace ID.
     * @param spanId span ID.
     * @return {@code true} if the link exists, {@code false} otherwise.
     */
    boolean has(long traceIdHigh, long traceIdLow, long spanId);

    /**
     * Adds a new link to the collection.
     *
     * @param traceIdHigh high 64 bits of trace ID.
     * @param traceIdLow low 64 bits of trace ID.
     * @param spanId span ID.
     * @param traceState trace state.
     * @param attributes attributes to associate with the link.
     * @return newly created {@link Link}.
     */
    Link add(long traceIdHigh, long traceIdLow, long spanId, String traceState, Map<String, Object> attributes);

    /**
     * Gets a specific link by trace ID and span ID.
     *
     * @param traceIdHigh high 64 bits of trace ID.
     * @param traceIdLow low 64 bits of trace ID.
     * @param spanId span ID.
     * @return {@link Link} if found, {@code null} otherwise.
     */
    Link get(long traceIdHigh, long traceIdLow, int spanId);

    /**
     * Removes a specific link by trace ID and span ID.
     *
     * @param traceIdHigh high 64 bits of trace ID.
     * @param traceIdLow low 64 bits of trace ID.
     * @param spanId span ID.
     * @return {@code true} if the link was removed, {@code false} otherwise.
     */
    boolean remove(long traceIdHigh, long traceIdLow, int spanId);

}
