package io.github.pointertrace.siglet.impl.engine.component;

import io.github.pointertrace.siglet.impl.config.graph.BaseNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;

public abstract  class BaseGraphComponent<T extends BaseNode> extends BaseComponent implements GraphComponent<T> {

    private final T node;

    private final SigletContext sigletContext;

    public BaseGraphComponent(SigletContext sigletContext, T node) {
        super(sigletContext.getInterceptor());
        this.sigletContext = sigletContext;
        this.node = node;
    }

    @Override
    public T getNode() {
        return node;
    }

    @Override
    public String getName() {
        return node.getName();
    }

    public SigletContext getSigletContext() {
        return sigletContext;
    }
}
