package io.github.pointertrace.siglet.impl.engine;

import io.github.pointertrace.siglet.impl.config.graph.BaseNode;

public interface Component<T extends BaseNode> {

    T getNode();

    void start();

    void stop();

    State getState();

    String getName();

}
