package com.siglet.container.engine.pipeline.processor.groovy;

import com.siglet.api.ProcessorContext;
import com.siglet.api.ResultFactory;
import com.siglet.container.eventloop.processor.BaseEventloopProcessor;

public abstract class BaseGroovyBaseEventloopProcessor<C> extends BaseEventloopProcessor<C> {

    private final Compiler compiler;

    protected BaseGroovyBaseEventloopProcessor(ProcessorContext<C> context, ResultFactory resultFactory,
                                               Compiler compiler) {
        super(context, resultFactory);
        this.compiler = compiler;
    }

    protected BaseGroovyBaseEventloopProcessor(ProcessorContext<C> context, ResultFactory resultFactory) {
        this(context, resultFactory, new Compiler());
    }

    public Compiler getCompiler() {
        return compiler;
    }

}
