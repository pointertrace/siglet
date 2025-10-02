package io.github.pointertrace.siglet.impl.engine.exporter;

import io.github.pointertrace.siglet.impl.config.graph.ExporterNode;
import io.github.pointertrace.siglet.impl.engine.EngineElement;
import io.github.pointertrace.siglet.impl.engine.SignalDestination;

public interface Exporter extends EngineElement, SignalDestination {

    ExporterNode getNode();

}
