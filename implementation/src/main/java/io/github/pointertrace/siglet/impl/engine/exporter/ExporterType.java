package io.github.pointertrace.siglet.impl.engine.exporter;

import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ConfigDefinition;

public interface ExporterType {

    String getName();

    ConfigDefinition getConfigDefinition();

    ExporterCreator getExporterCreator();

}
