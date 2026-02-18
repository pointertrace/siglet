package io.github.pointertrace.siglet.api.signal.metric;

/**
 * Represents a single quantile/value pair inside a summary metric data point.
 */
public interface ValueAtQuantile {

    /**
     * Returns the quantile (0 to 1) represented by this value.
     *
     * @return quantile value.
     */
    double getQuantile();

    /**
     * Sets the quantile (0 to 1) represented by this value.
     *
     * @param quantile quantile value.
     * @return current instance of {@code ValueAtQuantile}.
     */
    ValueAtQuantile setQuantile(double quantile);

    /**
     * Returns the metric value stored for the quantile.
     *
     * @return metric value.
     */
    double getValue();

    /**
     * Sets the metric value stored for the quantile.
     *
     * @param value metric value.
     * @return current instance of {@code ValueAtQuantile}.
     */
    ValueAtQuantile setValue(double value);
}
