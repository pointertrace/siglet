package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.component.SignalEmitterFunction;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.BaseSigletProcessor;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class GroovyProcessor extends BaseSigletProcessor<Signal> {

    public GroovyProcessor(SigletContext sigletContext, ProcessorNode node,
                           Supplier<BiFunction<Signal, Context<?>, ProcessResult>> factory) {
        super(sigletContext, node, factory);
    }

    @Override
    protected Signal castSignal(Object signal) {
        if (signal instanceof Span span) {
            return span;
        } else {
            throw new SigletError(String.format("Signal must be type %s but it is %s", Span.class, signal.getClass()));
        }
    }

}
