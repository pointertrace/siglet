package io.github.pointertrace.siglet.container.engine.exporter;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.container.config.graph.ExporterNode;
import io.github.pointertrace.siglet.container.config.raw.DebugExporterConfig;
import io.github.pointertrace.siglet.container.config.raw.GrpcExporterConfig;
import io.github.pointertrace.siglet.container.engine.Context;
import io.github.pointertrace.siglet.container.engine.exporter.debug.DebugExporter;
import io.github.pointertrace.siglet.container.engine.exporter.grpc.GrpcExporter;

import java.util.HashMap;
import java.util.Map;

public class Exporters {

    private final Map<String, Exporter> exporterRegistry = new HashMap<>();

    public Exporter getExporter(String name) {
        return exporterRegistry.get(name);
    }


    public Exporter create(Context context, ExporterNode node) {
        String name = node.getName();
        if (exporterRegistry.containsKey(name)) {
            throw new SigletError("Receiver with name " + name + " already exists");
        }
        if (node.getConfig() instanceof DebugExporterConfig) {
            return exporterRegistry.put(name, new DebugExporter(node));
        } else if (node.getConfig() instanceof GrpcExporterConfig) {
            return exporterRegistry.put(name, new GrpcExporter(context, node));
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
