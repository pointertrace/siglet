package com.siglet.data.modifiable;

import com.siglet.data.unmodifiable.UnmodifiableLink;

public interface ModifiableLink extends UnmodifiableLink {


    void setTraceId(long high, long low);

    void setSpanId(long spanId);

    void setTraceState(String traceState);

    void setFlags(int flags);
    void setDroppedAttributesCount(int droppedAttributesCount);

    ModifiableAttributes getAttributes();

}
