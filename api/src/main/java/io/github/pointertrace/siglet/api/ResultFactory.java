package io.github.pointertrace.siglet.api;

/**
 * Factory to create results for a result for a processed signal
 */
public interface ResultFactory {

    /**
     * Create a result indicating that the current signal must be dropped and not be
     * processed by following pipeline elements.
     *
     * @return Result indicating that the current signal must be dropped.
     */
    Result drop();

    /**
     * Create a result indicating that the current signal must proceed in the pipeline
     * processed by following pipeline elements using the only route available.
     *
     * @return Result indicating that the current signal must be processed
     */
    Result proceed();

    /**
     * Create a result indicating the current signal must be processed by following
     * pipeline elements using the {@code destination} route.
     *
     * @param destination Route to be used to propagate the current signal
     *
     * @return result indicating the current signal must be processed by following pipeline
     * elements using the {@code destination} route.
     */
    Result proceed(String destination);

}
