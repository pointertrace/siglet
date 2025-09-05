package io.github.pointertrace.siglet.api.signal;

public interface Event {

    long getTimeUnixNano() ;

    Event setTimeUnixNano(long timeUnixNano) ;

    String getName() ;

    Event setName(String name) ;

    int getDroppedAttributesCount() ;

    Event setDroppedAttributesCount(int droppedAttributesCount) ;

    Attributes getAttributes() ;

}
