package io.github.pointertrace.siglet.api.signal.metric;

import io.github.pointertrace.siglet.api.Signal;

/**
 * Represents a metric signal containing measurement data.
 */
public interface Metric extends Signal {

    /**
     * Returns the name of this metric.
     *
     * @return metric name.
     */
    String getName();

    /**
     * Sets the name of this metric.
     *
     * @param name metric name.
     * @return current instance of {@code Metric}.
     */
    Metric setName(String name);

    /**
     * Returns the description of this metric.
     *
     * @return metric description.
     */
    String getDescription();

    /**
     * Sets the description of this metric.
     *
     * @param description metric description.
     * @return current instance of {@code Metric}.
     */
    Metric setDescription(String description);

    /**
     * Returns the unit of this metric.
     *
     * @return metric unit.
     */
    String getUnit();

    /**
     * Sets the unit of this metric.
     *
     * @param unit metric unit.
     * @return current instance of {@code Metric}.
     */
    Metric setUnit(String unit);

    /**
     * Returns the data associated with this metric.
     *
     * @return metric data.
     */
    Data getData();

    /**
     * Returns whether this metric contains gauge data.
     *
     * @return {@code true} if the metric contains gauge data, {@code false} otherwise.
     */
    boolean hasGauge();

    /**
     * Returns the gauge data associated with this metric.
     *
     * @return gauge data.
     */
    Gauge getGauge();

    /**
     * Returns whether this metric contains sum data.
     *
     * @return {@code true} if the metric contains sum data, {@code false} otherwise.
     */
    boolean hasSum();

    /**
     * Returns the sum data associated with this metric.
     *
     * @return sum data.
     */
    Sum getSum();

    /**
     * Returns whether this metric contains histogram data.
     *
     * @return {@code true} if the metric contains histogram data, {@code false} otherwise.
     */
    boolean hasHistogram();

    /**
     * Returns the histogram data associated with this metric.
     *
     * @return histogram data.
     */
    Histogram getHistogram();

    /**
     * Returns whether this metric contains exponential histogram data.
     *
     * @return {@code true} if the metric contains exponential histogram data, {@code false} otherwise.
     */
    boolean hasExponentialHistogram();

    /**
     * Returns the exponential histogram data associated with this metric.
     *
     * @return exponential histogram data.
     */
    ExponentialHistogram getExponentialHistogram();

    /**
     * Returns whether this metric contains summary data.
     *
     * @return {@code true} if the metric contains summary data, {@code false} otherwise.
     */
    boolean hasSummary();

    /**
     * Returns the summary data associated with this metric.
     *
     * @return summary data.
     */
    Summary getSummary();
}
