package com.siglet.container.engine.exporter;

import com.siglet.api.Signal;
import com.siglet.container.config.graph.ExporterNode;
import com.siglet.container.engine.EngineElement;
import com.siglet.container.engine.SignalDestination;

public interface Exporter extends EngineElement, SignalDestination {

    ExporterNode getNode();

}
