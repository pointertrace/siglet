package io.github.pointertrace.siglet.impl.engine;

import io.github.pointertrace.siglet.impl.config.graph.BaseNode;

@FunctionalInterface
public interface ComponentCreator<T extends BaseNode> {

    Component<T> create(SigletContext sigletContext, T node);

}
