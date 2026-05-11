package io.github.pointertrace.siglet.impl.engine.exporter;

import io.github.pointertrace.siglet.impl.engine.ComponentTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.exporter.debug.DebugExporterType;
import io.github.pointertrace.siglet.impl.engine.exporter.grpc.OtelGrpcExporterType;

public class ExporterTypeRegistry extends ComponentTypeRegistry<ExporterType<?>> {

    public ExporterTypeRegistry() {
        register(new DebugExporterType());
        register(new OtelGrpcExporterType());
    }
}
