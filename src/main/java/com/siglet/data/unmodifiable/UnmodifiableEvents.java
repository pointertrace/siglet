package com.siglet.data.unmodifiable;

public interface UnmodifiableEvents {

    int size();

    UnmodifiableEvent get(int i);

    UnmodifiableEvent remove(int i);

}
