package io.github.pointertrace.siglet.container.engine.exporter;

import io.github.pointertrace.siglet.container.engine.pipeline.processor.ConfigDefinition;

public interface ExporterType {

    String getName();

    ConfigDefinition getConfigDefinition();

    ExporterCreator getExporterCreator();

}
