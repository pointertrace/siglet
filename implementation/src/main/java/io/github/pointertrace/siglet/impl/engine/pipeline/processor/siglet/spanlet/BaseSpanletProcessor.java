package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.spanlet;


import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.impl.eventloop.processor.BaseProcessor;
import io.github.pointertrace.siglet.impl.eventloop.processor.result.ResultFactoryImpl;

public class BaseSpanletProcessor<T> extends BaseProcessor<T> {

    private final Spanlet<T> spanlet;

    public BaseSpanletProcessor(Context<T> context, Spanlet<T> spanlet) {
        super(context, ResultFactoryImpl.INSTANCE);
        this.spanlet = spanlet;
    }

    @Override
    protected Result process(Signal signal, Context<T> context, ResultFactory resultFactory) {
        // TODO checar tipo do signal
        return spanlet.span((Span) signal, context, resultFactory);
    }
}
