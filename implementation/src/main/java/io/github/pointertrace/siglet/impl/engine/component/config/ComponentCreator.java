package io.github.pointertrace.siglet.impl.engine.component.config;

import io.github.pointertrace.siglet.impl.config.graph.BaseNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.component.GraphComponent;

@FunctionalInterface
public interface ComponentCreator<T extends BaseNode> {

    GraphComponent<T> create(SigletContext sigletContext, T node);

}
