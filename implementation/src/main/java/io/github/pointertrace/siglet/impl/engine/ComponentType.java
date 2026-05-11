package io.github.pointertrace.siglet.impl.engine;

import io.github.pointertrace.siglet.impl.config.graph.BaseNode;

public interface ComponentType<T,C extends BaseNode> {

    String getType();

    ConfigurationFactory<T> getConfigurationFactory();

    ComponentCreator<C> getComponentCreator();

}
