package io.github.pointertrace.siglet.api.signal.metric;

/**
 * Represents a gauge metric data type capturing instantaneous measurement values.
 */
public interface Gauge extends Data {

    /**
     * Returns the collection of data points associated with this gauge metric.
     *
     * @return collection of {@link NumberDataPoint} elements.
     */
    NumberDataPoints getDataPoints();

}
