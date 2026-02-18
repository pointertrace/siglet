package io.github.pointertrace.siglet.api.signal.trace;

import io.github.pointertrace.siglet.api.Siglet;
import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;

/**
 * A siglet that processes {@link Span} signals.
 *
 * @param <T> the type of configuration used by this spanlet.
 */
public interface Spanlet<T> extends Siglet {

    /**
     * Processes a span signal and produces a result.
     *
     * @param span the span signal to process.
     * @param context the processing context containing configuration.
     * @param resultFactory factory for creating result instances.
     * @return the result of processing the span.
     */
    Result span(Span span, Context<T> context, ResultFactory resultFactory);

}
