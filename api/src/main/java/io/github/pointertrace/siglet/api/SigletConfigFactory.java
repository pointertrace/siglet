package io.github.pointertrace.siglet.api;

public interface SigletConfigFactory<T> {

    T createConfig(String yaml);

}
