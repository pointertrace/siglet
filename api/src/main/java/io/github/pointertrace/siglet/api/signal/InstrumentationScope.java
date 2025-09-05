package io.github.pointertrace.siglet.api.signal;

public interface InstrumentationScope {

    String getName() ;

    InstrumentationScope setName(String name) ;

    String getVersion() ;

    InstrumentationScope setVersion(String version) ;

    int getDroppedAttributesCount() ;

    InstrumentationScope setDroppedAttributesCount(int droppedAttributesCount);

    Attributes getAttributes() ;

}
