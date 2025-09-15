package io.github.pointertrace.siglet.api;

import java.util.concurrent.ConcurrentMap;

/**
 * Defines siglet context. The context allows for the siglets to manage attributes
 * and to get the configuration informed when the siglet is configured
 *
 *
 * @param <T> Type of configuration
 */
public interface ProcessorContext<T> {

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

}
