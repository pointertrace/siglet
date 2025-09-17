package io.github.pointertrace.siglet.container.engine.exporter;

import io.github.pointertrace.siglet.container.config.graph.ExporterNode;
import io.github.pointertrace.siglet.container.engine.Context;

public interface ExporterCreator {

    Exporter create(Context context, ExporterNode exporterNode);
}
