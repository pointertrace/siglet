package io.github.pointertrace.siglet.impl.engine.component.config;

import io.github.pointertrace.siglet.impl.config.graph.BaseNode;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;

public interface ComponentType<T,C extends BaseNode> {

    String getType();

    ConfigurationFactory<T> getConfigurationFactory();

    ComponentCreator<C> getComponentCreator();

}
