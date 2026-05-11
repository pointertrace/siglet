package io.github.pointertrace.siglet.impl.config.graph;

import io.github.pointertrace.siglet.impl.config.descriptor.ExporterDescriptor;

import java.util.ArrayList;
import java.util.List;

public final class ExporterNode extends BaseNode {

    private List<BaseNode> from = new ArrayList<>();

    public ExporterNode(ExporterDescriptor exporterDescriptor) {
        super(exporterDescriptor);
    }

    public List<BaseNode> getFrom() {
        return from;
    }

    public void setFrom(List<BaseNode> from) {
        this.from = from;
    }

    @Override
    public ExporterDescriptor getDescription() {
        return (ExporterDescriptor) super.getDescription();
    }

}
