package io.github.pointertrace.siglet.impl.config.graph;


import io.github.pointertrace.siglet.impl.config.descriptor.BaseDescriptor;

public abstract sealed class BaseNode permits ReceiverNode, PipelineNode, ProcessorNode, ExporterNode {


    private final BaseDescriptor descriptor;

    protected BaseNode(BaseDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public String getName() {
        return descriptor.getName().getValue();
    }


    public BaseDescriptor getDescription() {
        return descriptor;
    }
}
