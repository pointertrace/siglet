package io.github.pointertrace.siglet.container.example.bundle.fatjar.suffix.siglet;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;

/**
 * SuffixSpanlet is an implementation of the Spanlet interface. It processes span signals by appending
 * a suffix and the string "-fatjar" to the name of the span. The behavior of the suffix addition
 * is defined by the SuffixSpanletConfig passed through the context during the processing.
 *
 * This class modifies the span's name to include additional descriptive information, often useful
 * for categorization, debugging, or monitoring in distributed tracing systems.
 *
 * It executes its processing logic using the following sequence:
 * 1. Retrieves the span name.
 * 2. Appends the configured suffix along with "-fatjar".
 * 3. Updates the span name.
 * 4. Proceeds to the next steps in the signal pipeline.
 *
 * The class utilizes the following key method:
 * - {@code span}: Processes a {@code Span} signal by modifying its name and proceeds.
 *
 * Implements the {@code Spanlet} interface with the following type parameter:
 * - {@code SuffixSpanletConfig}: Configuration holding the suffix text used during the name modification.
 */
public class SuffixSpanlet implements Spanlet<SuffixSpanletConfig> {


    @Override
    public Result span(Span span, Context<SuffixSpanletConfig> context, ResultFactory resultFactory) {
        span.setName(span.getName() + context.getConfig().getSuffix() + "-fatjar");
        return resultFactory.proceed();
    }

}
