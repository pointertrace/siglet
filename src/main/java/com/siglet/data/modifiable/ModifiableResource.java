package com.siglet.data.modifiable;

import com.siglet.data.unmodifiable.UnmodifiableResource;

public interface ModifiableResource extends UnmodifiableResource {

    ModifiableAttributes getAttributes();

    void setDroppedAttributesCount(int droppedAttributesCount);
}
