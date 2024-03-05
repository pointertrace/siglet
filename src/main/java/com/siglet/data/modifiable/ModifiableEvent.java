package com.siglet.data.modifiable;

import com.siglet.data.unmodifiable.UnmodifiableEvent;

public interface ModifiableEvent extends UnmodifiableEvent {

    void setTimeUnixNano(long timeUnixNano) ;

    void setName(String name) ;

    void setDroppedAttributesCount(int droppedAttributesCount) ;

    ModifiableAttributes getAttributes() ;

}
