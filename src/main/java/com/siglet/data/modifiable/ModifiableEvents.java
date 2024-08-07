package com.siglet.data.modifiable;

import com.siglet.data.unmodifiable.UnmodifiableEvents;

import java.util.Map;

public interface ModifiableEvents extends UnmodifiableEvents {

    ModifiableEvent get(int i);

    void remove(int i);

    ModifiableEvent add();
}
