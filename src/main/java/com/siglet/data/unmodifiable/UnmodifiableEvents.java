package com.siglet.data.unmodifiable;

public interface UnmodifiableEvents {

    int size();

    UnmodifiableEvent get(int i);

    // TODO remover
    UnmodifiableEvent remove(int i);

}
