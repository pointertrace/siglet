package io.github.pointertrace.siglet.impl.engine.component;

import io.github.pointertrace.siglet.impl.engine.State;

public interface Component {

    void start();

    void stop();

    State getState();

    String getName();

}
