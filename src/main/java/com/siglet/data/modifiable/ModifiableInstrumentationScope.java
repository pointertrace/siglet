package com.siglet.data.modifiable;

import com.siglet.data.unmodifiable.UnmodifiableInstrumentationScope;

public interface ModifiableInstrumentationScope extends UnmodifiableInstrumentationScope {

    ModifiableInstrumentationScope setName(String name) ;

    ModifiableInstrumentationScope setVersion(String version) ;

    ModifiableInstrumentationScope setDroppedAttributesCount(int droppedAttributesCount);

    ModifiableAttributes getAttributes() ;

}
