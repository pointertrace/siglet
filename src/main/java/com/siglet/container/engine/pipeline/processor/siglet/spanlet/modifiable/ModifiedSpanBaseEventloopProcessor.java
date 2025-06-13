package com.siglet.container.engine.pipeline.processor.siglet.spanlet.modifiable;


import com.siglet.api.ProcessorContext;
import com.siglet.api.Result;
import com.siglet.api.ResultFactory;
import com.siglet.api.Signal;
import com.siglet.api.modifiable.trace.ModifiableSpan;
import com.siglet.api.modifiable.trace.ModifiableSpanlet;
import com.siglet.container.eventloop.processor.BaseEventloopProcessor;
import com.siglet.container.eventloop.processor.result.ResultFactoryImpl;

public class ModifiedSpanBaseEventloopProcessor<T> extends BaseEventloopProcessor<T> {

    private final ModifiableSpanlet<T> spanlet;

    public ModifiedSpanBaseEventloopProcessor(ProcessorContext<T> context, ModifiableSpanlet<T> spanlet) {
        super(context, ResultFactoryImpl.INSTANCE);
        this.spanlet = spanlet;
    }

    @Override
    protected Result process(Signal signal, ProcessorContext<T> context, ResultFactory resultFactory) {
        // TODO checar tipo do signal
        return spanlet.span((ModifiableSpan) signal, context, resultFactory);
    }
}
