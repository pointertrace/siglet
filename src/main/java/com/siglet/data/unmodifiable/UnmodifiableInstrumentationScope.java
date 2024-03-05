package com.siglet.data.unmodifiable;

public interface UnmodifiableInstrumentationScope {

    String getName() ;

    String getVersion() ;

    int getDroppedAttributesCount() ;

    UnmodifiableAttributes getAttributes() ;
}
