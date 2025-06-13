package com.siglet.container.engine.exporter;

import com.siglet.SigletError;
import com.siglet.container.config.graph.ExporterNode;
import com.siglet.container.config.raw.DebugExporterConfig;
import com.siglet.container.engine.exporter.debug.DebugExporter;

import java.util.HashMap;
import java.util.Map;

public class Exporters {

    private final Map<String, Exporter> exporters = new HashMap<>();

    public Exporter getExporter(String name) {
        return exporters.get(name);
    }

    public Exporter create(ExporterNode node) {
        String name = node.getName();
        if (exporters.containsKey(name)) {
            throw new SigletError("Receiver with name " + name+ " already exists");
        }
        if (node.getConfig() instanceof DebugExporterConfig debugExporterItem) {
            return exporters.put(name, new DebugExporter(node));
        } else {
            throw new SigletError(String.format("Cannot create receiver type %s",node.getConfig().getClass().getName()));
        }
    }

    public void start() {
        exporters.values().forEach(Exporter::start);
    }

    public void stop() {
        exporters.values().forEach(Exporter::stop);
    }
}
