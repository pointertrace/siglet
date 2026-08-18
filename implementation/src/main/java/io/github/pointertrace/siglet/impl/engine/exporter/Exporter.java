package io.github.pointertrace.siglet.impl.engine.exporter;

import io.github.pointertrace.siglet.impl.engine.component.GraphComponent;
import io.github.pointertrace.siglet.impl.config.graph.ExporterNode;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestinationProvider;

public interface Exporter extends GraphComponent<ExporterNode>, SignalDestinationProvider {

}
