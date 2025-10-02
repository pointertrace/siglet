package io.github.pointertrace.siglet.impl.engine.exporter;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.graph.ExporterNode;
import io.github.pointertrace.siglet.impl.engine.Context;
import io.github.pointertrace.siglet.impl.engine.exporter.debug.DebugExporterType;
import io.github.pointertrace.siglet.impl.engine.exporter.grpc.OtelGrpcExporterType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExporterTypeRegistry {

    private final Map<String, ExporterType> definitions = new HashMap<>();

    public ExporterTypeRegistry() {
        register(new DebugExporterType());
        register(new OtelGrpcExporterType());
    }

    public Set<String> getExporterTypesNames() {
        return Collections.unmodifiableSet(definitions.keySet());
    }

    public void register(ExporterType exporterType) {
        definitions.put(exporterType.getName(), exporterType);
    }

    public ExporterType get(String type) {
        return definitions.get(type);
    }

    public Exporter create(Context context, ExporterNode exporterNode) {
        ExporterType exporterType = definitions.get(exporterNode.getConfig().getType());
        if (exporterType == null) {
            throw new SigletError("Receiver type " + exporterNode.getConfig().getType() + " not found");
        }
        return exporterType.getExporterCreator().create(context, exporterNode);
    }


}
