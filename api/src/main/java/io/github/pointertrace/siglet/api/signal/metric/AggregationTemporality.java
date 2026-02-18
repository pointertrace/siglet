package io.github.pointertrace.siglet.api.signal.metric;

/**
 * Defines the aggregation temporality for a metric, indicating how data points are aggregated over time.
 */
public enum AggregationTemporality {

    /**
     * Indicates that the metric data points represent changes since the last reported value.
     */
    DELTA,

    /**
     * Indicates that the metric data points represent cumulative values since a fixed start time.
     */
    CUMULATIVE;

}
