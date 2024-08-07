package com.siglet.data.modifiable;

import com.siglet.data.unmodifiable.UnmodifiableEvent;

public interface ModifiableEvent extends UnmodifiableEvent {

    ModifiableEvent setTimeUnixNano(long timeUnixNano) ;

    ModifiableEvent setName(String name) ;

    ModifiableEvent setDroppedAttributesCount(int droppedAttributesCount) ;

    ModifiableAttributes getAttributes() ;

}
