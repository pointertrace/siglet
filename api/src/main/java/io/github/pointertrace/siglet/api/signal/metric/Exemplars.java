package io.github.pointertrace.siglet.api.signal.metric;

public interface Exemplars {

    int getSize();

    Exemplar get(int i);

    void remove(int i);

}
