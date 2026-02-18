package io.github.pointertrace.siglet.api.signal.metric;

/**
 * Represents a summary metric data type containing quantile information.
 */
public interface Summary extends Data {

    /**
     * Returns the collection of data points associated with this summary metric.
     *
     * @return collection of {@link SummaryDataPoint} elements.
     */
    SummaryDataPoints getDataPoints();

}
