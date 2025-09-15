package io.github.pointertrace.siglet.api;

/**
 * Represents the result of a processed signal
 *
 */
public interface Result {

        /**
         * Used to indicate that the result, beyond doing the default action, will send
         * another {@code signal} to {@code destination}
         *
         * @param signal Signal to be sent
         * @param destination Signal destination
         * @return New result
         * @param <T> Type of signal
         */
        <T extends Signal> Result andSend(T signal, String destination);

}
