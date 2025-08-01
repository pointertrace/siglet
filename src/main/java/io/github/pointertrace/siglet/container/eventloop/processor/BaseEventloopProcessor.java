package io.github.pointertrace.siglet.container.eventloop.processor;

import io.github.pointertrace.siglet.api.ProcessorContext;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.Signal;

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
