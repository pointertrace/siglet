package io.github.pointertrace.siglet.impl.engine.exporter;

import io.github.pointertrace.siglet.impl.config.graph.ExporterNode;
import io.github.pointertrace.siglet.impl.engine.Context;

public interface ExporterCreator {

    Exporter create(Context context, ExporterNode exporterNode);
}
