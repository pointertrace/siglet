package com.siglet.container.engine.pipeline.processor.groovy;

import com.siglet.api.ProcessorContext;
import com.siglet.api.ResultFactory;
import com.siglet.container.eventloop.processor.BaseEventloopProcessor;

public abstract class BaseGroovyBaseEventloopProcessor<CTX> extends BaseEventloopProcessor<CTX> {

     private final Compiler compiler;

    public BaseGroovyBaseEventloopProcessor(ProcessorContext<CTX> context, ResultFactory resultFactory, Compiler compiler) {
        super(context, resultFactory);
        this.compiler = compiler;
    }

    public BaseGroovyBaseEventloopProcessor(ProcessorContext<CTX> context, ResultFactory resultFactory) {
        this(context, resultFactory, new Compiler());
    }

    public Compiler getCompiler() {
        return compiler;
    }

}
