package com.siglet.container.engine.pipeline.processor.siglet.spanlet;


import com.siglet.api.ProcessorContext;
import com.siglet.api.Result;
import com.siglet.api.ResultFactory;
import com.siglet.api.Signal;
import com.siglet.api.data.trace.Span;
import com.siglet.api.data.trace.Spanlet;
import com.siglet.container.eventloop.processor.BaseEventloopProcessor;
import com.siglet.container.eventloop.processor.result.ResultFactoryImpl;

public class ModifiedSpanBaseEventloopProcessor<T> extends BaseEventloopProcessor<T> {

    private final Spanlet<T> spanlet;

    public ModifiedSpanBaseEventloopProcessor(ProcessorContext<T> context, Spanlet<T> spanlet) {
        super(context, ResultFactoryImpl.INSTANCE);
        this.spanlet = spanlet;
    }

    @Override
    protected Result process(Signal signal, ProcessorContext<T> context, ResultFactory resultFactory) {
        // TODO checar tipo do signal
        return spanlet.span((Span) signal, context, resultFactory);
    }
}
