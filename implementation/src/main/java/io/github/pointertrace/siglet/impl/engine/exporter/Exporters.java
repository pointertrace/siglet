package io.github.pointertrace.siglet.impl.engine.exporter;

import io.github.pointertrace.siglet.impl.config.graph.ExporterNode;
import io.github.pointertrace.siglet.impl.engine.Context;

import java.util.HashMap;
import java.util.Map;

public class Exporters {

    private final Map<String, Exporter> exporterRegistry = new HashMap<>();

    public Exporter getExporter(String name) {
        return exporterRegistry.get(name);
    }


    public Exporter create(Context context, ExporterNode exporterNode) {
        return exporterRegistry.put(exporterNode.getName(), context.createExporter(exporterNode));
    }

    public void start() {
        exporterRegistry.values().forEach(Exporter::start);
    }

    public void stop() {
        exporterRegistry.values().forEach(Exporter::stop);
    }
}
