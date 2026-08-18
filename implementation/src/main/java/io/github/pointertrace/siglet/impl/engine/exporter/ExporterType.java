package io.github.pointertrace.siglet.impl.engine.exporter;

import io.github.pointertrace.siglet.impl.config.graph.ExporterNode;
import io.github.pointertrace.siglet.impl.engine.component.config.ComponentType;

public interface ExporterType<T> extends ComponentType<T, ExporterNode> {

}
