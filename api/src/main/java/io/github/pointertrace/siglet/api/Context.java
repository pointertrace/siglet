package io.github.pointertrace.siglet.api;

import io.github.pointertrace.siglet.api.signal.metric.Metric;

import java.util.concurrent.ConcurrentMap;

/**
 * Represents a context that provides configuration and attributes, as well as methods to create metric signals.
 *
 * @param <T> The type of configuration object associated with this context.
 */
public interface Context<T> {

    /**
     * Returns a ConcurrentMap that contains attributes associated with the siglet
     *
     * @return  ConcurrentMap that contains attributes associated with the siglet
     */
    ConcurrentMap<String, Object> getAttributes();

    /**
     * Returns the instance of the object configured when the siglet is used
     *
     * @return Instance of the object configured when the siglet is used
     */
    T getConfig();

    /**
     * Creates new Gauge metric signal coping information such as Resource and Instrumentation Scope from baseSignal.
     *
     * @param baseSignal Signal to get information from
     *
     * @return Gauge metric signal
     */
    Metric newGauge(Signal baseSignal);

    /**
     * Creates new gauge metric signal.
     *
     * @return Gauge Metric signal
     */
    Metric newGauge();

    /**
     * Creates new sum metric signal coping information such as Resource and Instrumentation Scope from
     * baseSignal.
     *
     * @param baseSignal Signal to get information from
     *
     * @return Sum Metric signal
     */
    Metric newSum(Signal baseSignal);

    /**
     * Creates new sum metric signal.
     *
     * @return Sum Metric signal
     */
    Metric newSum();

}
