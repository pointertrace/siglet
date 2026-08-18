package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.spanlet;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.BaseSigletProcessor;

import java.util.function.BiFunction;

public class SpanletProcessor extends BaseSigletProcessor<Span> {


    public SpanletProcessor(SigletContext sigletContext, ProcessorNode node, BiFunction<Span, Context<?>, ProcessResult> sigletExecutionFunction) {
        super(sigletContext, node, sigletExecutionFunction);
    }

    @Override
    protected Span castSignal(Object signal) {
        if (signal instanceof Span s) {
            return s;
        } else {
            throw new SigletError(String.format("Signal must be type %s but it is %s", Span.class, signal.getClass()));
        }
    }
}
