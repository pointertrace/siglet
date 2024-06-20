package com.siglet.data.modifiable.trace;

import com.siglet.data.modifiable.ModifiableAttributes;
import com.siglet.data.unmodifiable.trace.UnmodifiableLink;

public interface ModifiableLink extends UnmodifiableLink {


    void setTraceId(long high, long low);

    void setSpanId(long spanId);

    void setTraceState(String traceState);

    void setFlags(int flags);
    void setDroppedAttributesCount(int droppedAttributesCount);

    ModifiableAttributes getAttributes();

}
