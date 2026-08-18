package io.github.pointertrace.siglet.impl.engine.component;

import io.github.pointertrace.siglet.impl.config.graph.BaseNode;

public interface GraphComponent<T extends BaseNode> extends Component {

    T getNode();
}
