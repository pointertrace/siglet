package com.siglet.data.unmodifiable;

public interface UnmodifiableEvent {

    long getTimeUnixNano() ;

    String getName() ;

    int getDroppedAttributesCount() ;

    UnmodifiableAttributes getAttributes() ;
}
