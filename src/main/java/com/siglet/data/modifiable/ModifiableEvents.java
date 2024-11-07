package com.siglet.data.modifiable;

import com.siglet.data.unmodifiable.UnmodifiableEvents;

public interface ModifiableEvents extends UnmodifiableEvents {

    ModifiableEvent get(int i);

    void remove(int i);

    ModifiableEvent add();
}
