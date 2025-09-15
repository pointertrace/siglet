package io.github.pointertrace.siglet.api;

/**
 * Base interface for a signal that can be processed by a { @Link Processor}
 */
public interface Signal {

    /**
     * Signal identification
     *
     * @return Signal identification
     */
    String getId();
}
