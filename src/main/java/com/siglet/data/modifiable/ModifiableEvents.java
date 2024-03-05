package com.siglet.data.modifiable;

import com.siglet.data.unmodifiable.UnmodifiableEvent;
import com.siglet.data.unmodifiable.UnmodifiableEvents;

import java.util.Map;

public interface ModifiableEvents extends UnmodifiableEvents {

    ModifiableEvent get(int i);

    ModifiableEvent remove(int i);

    void add(String name, long timeUnixNano, int droppedAttributesCount, Map<String, Object> attributes);
}
