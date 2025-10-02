package io.github.pointertrace.siglet.impl.engine;

public interface EngineElement {

    void start();

    void stop();

    State getState();

    String getName();

}
