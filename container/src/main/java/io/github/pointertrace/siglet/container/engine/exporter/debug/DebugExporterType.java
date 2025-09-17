package io.github.pointertrace.siglet.container.engine.exporter.debug;

import io.github.pointertrace.siglet.container.engine.exporter.ExporterCreator;
import io.github.pointertrace.siglet.container.engine.exporter.ExporterType;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ConfigDefinition;
import io.github.pointertrace.siglet.parser.schema.EmptyPropertyChecker;

public class DebugExporterType implements ExporterType {


    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public ConfigDefinition getConfigDefinition() {
        return () -> new EmptyPropertyChecker("config");
    }

    @Override
    public ExporterCreator getExporterCreator() {
        return (context, node) -> new DebugExporter(node);
    }

}
