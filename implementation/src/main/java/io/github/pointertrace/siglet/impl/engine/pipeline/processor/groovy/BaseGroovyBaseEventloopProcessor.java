package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.impl.eventloop.processor.BaseEventloopProcessor;

public abstract class BaseGroovyBaseEventloopProcessor<C> extends BaseEventloopProcessor<C> {

    private final Compiler compiler;

    protected BaseGroovyBaseEventloopProcessor(Context<C> context, ResultFactory resultFactory,
                                               Compiler compiler) {
        super(context, resultFactory);
        this.compiler = compiler;
    }

    protected BaseGroovyBaseEventloopProcessor(Context<C> context, ResultFactory resultFactory) {
        this(context, resultFactory, new Compiler());
    }

    public Compiler getCompiler() {
        return compiler;
    }

}
