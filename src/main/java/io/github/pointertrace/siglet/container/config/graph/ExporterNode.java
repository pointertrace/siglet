package io.github.pointertrace.siglet.container.config.graph;

import io.github.pointertrace.siglet.container.config.raw.ExporterConfig;

import java.util.ArrayList;
import java.util.List;

public final class ExporterNode extends BaseNode {

    private List<BaseNode> from = new ArrayList<>();

    public ExporterNode(ExporterConfig exporterConfig) {
        super(exporterConfig);
    }

    public List<BaseNode> getFrom() {
        return from;
    }

    public void setFrom(List<BaseNode> from) {
        this.from = from;
    }

    @Override
    public ExporterConfig getConfig() {
        return (ExporterConfig) super.getConfig();
    }

}
