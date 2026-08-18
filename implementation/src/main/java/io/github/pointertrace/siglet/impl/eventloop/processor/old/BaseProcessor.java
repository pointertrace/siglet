package io.github.pointertrace.siglet.impl.eventloop.processor.old;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.Signal;

public abstract class BaseProcessor<CTX> {

    private final Context<CTX> context;

    private final ResultFactory resultFactory;

    public BaseProcessor(Context<CTX> context, ResultFactory resultFactory) {
        this.context = context;
        this.resultFactory = resultFactory;
    }

    public final Result process(Signal signal) {
        return process(signal, context, resultFactory);
    }

    protected abstract Result process(Signal signal, Context<CTX> context, ResultFactory resultFactory);

}
