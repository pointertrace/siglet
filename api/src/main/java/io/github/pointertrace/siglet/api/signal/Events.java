package io.github.pointertrace.siglet.api.signal;

public interface Events {

    int getSize();

    Event get(int i);

    void remove(int i);

    Event add();
}
