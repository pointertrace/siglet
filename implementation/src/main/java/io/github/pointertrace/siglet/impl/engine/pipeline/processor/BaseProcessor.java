package io.github.pointertrace.siglet.impl.engine.pipeline.processor;

import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.component.BaseGraphComponent;

public abstract class BaseProcessor extends BaseGraphComponent<ProcessorNode> implements Processor {

    public BaseProcessor(SigletContext sigletContext, ProcessorNode processorNode) {
        super(sigletContext, processorNode);
    }

    @Override
    protected void doStart() {
    }

    @Override
    protected void doStop() {
    }

}
