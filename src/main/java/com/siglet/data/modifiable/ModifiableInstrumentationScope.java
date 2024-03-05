package com.siglet.data.modifiable;

import com.siglet.data.unmodifiable.UnmodifiableInstrumentationScope;

public interface ModifiableInstrumentationScope extends UnmodifiableInstrumentationScope {

    void setName(String name) ;

    void setVersion(String version) ;

    void setDroppedAttributesCount(int droppedAttributesCount);

    ModifiableAttributes getAttributes() ;

}
