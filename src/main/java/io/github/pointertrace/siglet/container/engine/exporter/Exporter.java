package io.github.pointertrace.siglet.container.engine.exporter;

import io.github.pointertrace.siglet.container.config.graph.ExporterNode;
import io.github.pointertrace.siglet.container.engine.EngineElement;
import io.github.pointertrace.siglet.container.engine.SignalDestination;

public interface Exporter extends EngineElement, SignalDestination {

    ExporterNode getNode();

}
