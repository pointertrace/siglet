package io.github.pointertrace.siglet.api.signal;

public interface Resource {

    int getDroppedAttributesCount();

    Resource setDroppedAttributesCount(int droppedAttributesCount);

    Attributes getAttributes();
}
