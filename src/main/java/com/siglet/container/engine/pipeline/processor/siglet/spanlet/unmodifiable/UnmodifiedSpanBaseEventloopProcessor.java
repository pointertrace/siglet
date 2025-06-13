package com.siglet.container.engine.pipeline.processor.siglet.spanlet.unmodifiable;


import com.siglet.api.ProcessorContext;
import com.siglet.api.Result;
import com.siglet.api.ResultFactory;
import com.siglet.api.Signal;
import com.siglet.api.unmodifiable.trace.UnmodifiableSpan;
import com.siglet.api.unmodifiable.trace.UnmodifiableSpanlet;
import com.siglet.container.eventloop.processor.BaseEventloopProcessor;
import com.siglet.container.eventloop.processor.result.ResultFactoryImpl;

public class UnmodifiedSpanBaseEventloopProcessor<T> extends BaseEventloopProcessor<T> {

    private final UnmodifiableSpanlet<T> spanlet;

    public UnmodifiedSpanBaseEventloopProcessor(ProcessorContext<T> context, UnmodifiableSpanlet<T> spanlet) {
        super(context, ResultFactoryImpl.INSTANCE);
        this.spanlet = spanlet;
    }

    @Override
    protected Result process(Signal signal, ProcessorContext<T> context, ResultFactory resultFactory) {
        // TODO checar tipo do signal
        return spanlet.span((UnmodifiableSpan) signal,context, resultFactory);

    }
}
