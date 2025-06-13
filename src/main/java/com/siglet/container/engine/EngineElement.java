package com.siglet.container.engine;

public interface EngineElement {

    void start();

    void stop();

    State getState();

    String getName();

}
