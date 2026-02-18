package io.github.pointertrace.siglet.api;

/**
 * Represents the result of a processed signal
 *
 * Represents the outcome decided by a siglet after processing a signal.
 */
public interface Result {

        /**
         * Used to indicate that the result, beyond doing the default action, will send
         * another {@code signal} to {@code destination}
         * Produces a new result that, in addition to the default action, sends another {@code signal} to {@code destination}.
         *
         * @param signal Signal to be sent
         * @param destination Signal destination
         * @return New result
         * @param <T> Type of signal
         * @param signal signal to be sent.
         * @param destination signal destination.
         * @param <T> type of the signal being sent.
         * @return result describing the default action plus the additional send instruction.
         */
        <T extends Signal> Result andSend(T signal, String destination);

}
