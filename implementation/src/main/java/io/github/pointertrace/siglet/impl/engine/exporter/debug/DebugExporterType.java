package io.github.pointertrace.siglet.impl.engine.exporter.debug;

import io.github.pointertrace.siglet.impl.config.graph.ExporterNode;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.ComponentCreator;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterCreator;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterType;

public class DebugExporterType implements ExporterType<Void> {


    @Override
    public String getType() {
        return "debug";
    }

    @Override
    public ConfigurationFactory<Void> getConfigurationFactory() {
        return ConfigurationFactory.of();
    }

    @Override
    public ComponentCreator<ExporterNode> getComponentCreator() {
        return (context, node) -> new DebugExporter(node);
    }

}
