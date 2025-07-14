package com.siglet.container.engine.exporter;

import com.siglet.SigletError;
import com.siglet.container.config.graph.ExporterNode;
import com.siglet.container.config.raw.DebugExporterConfig;
import com.siglet.container.config.raw.GrpcExporterConfig;
import com.siglet.container.engine.exporter.debug.DebugExporter;
import com.siglet.container.engine.exporter.grpc.GrpcExporter;

import java.util.HashMap;
import java.util.Map;

public class Exporters {

    private final Map<String, Exporter> exporterRegistry = new HashMap<>();

    public Exporter getExporter(String name) {
        return exporterRegistry.get(name);
    }


    public Exporter create(ExporterNode node) {
        String name = node.getName();
        if (exporterRegistry.containsKey(name)) {
            throw new SigletError("Receiver with name " + name + " already exists");
        }
        if (node.getConfig() instanceof DebugExporterConfig) {
            return exporterRegistry.put(name, new DebugExporter(node));
        } else if (node.getConfig() instanceof GrpcExporterConfig) {
            return exporterRegistry.put(name, new GrpcExporter(node));
        } else {
            throw new SigletError(String.format("Cannot create receiver type %s", node.getConfig().getClass().getName()));
        }
    }

    public void start() {
        exporterRegistry.values().forEach(Exporter::start);
    }

    public void stop() {
        exporterRegistry.values().forEach(Exporter::stop);
    }
}
