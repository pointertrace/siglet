package com.siglet.container.eventloop.processor;

import com.siglet.api.ProcessorContext;
import com.siglet.api.Result;
import com.siglet.api.ResultFactory;
import com.siglet.api.Signal;

public abstract class BaseEventloopProcessor<CTX> {

    private final ProcessorContext<CTX> context;

    private final ResultFactory resultFactory;

    public BaseEventloopProcessor(ProcessorContext<CTX> context, ResultFactory resultFactory) {
        this.context = context;
        this.resultFactory = resultFactory;
    }

    public final Result process(Signal signal) {
        return process(signal, context, resultFactory);
    }

    protected abstract Result process(Signal signal, ProcessorContext<CTX> context, ResultFactory resultFactory);


}
