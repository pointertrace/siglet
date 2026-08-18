package io.github.pointertrace.siglet.impl.engine.exporter;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.config.graph.ExporterNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.engine.component.BaseGraphComponent;

public abstract class BaseExporter extends BaseGraphComponent<ExporterNode> implements Exporter {

    public BaseExporter(SigletContext sigletContext, ExporterNode node) {
        super(sigletContext, node);
    }

}
