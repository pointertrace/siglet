package com.siglet.data.modifiable.trace;

import com.siglet.data.modifiable.ModifiableAttributes;
import com.siglet.data.unmodifiable.trace.UnmodifiableLink;

public interface ModifiableLink extends UnmodifiableLink {


    ModifiableLink setTraceId(long high, long low);

    ModifiableLink setSpanId(long spanId);

    ModifiableLink setTraceState(String traceState);

    ModifiableLink setFlags(int flags);

    ModifiableLink setDroppedAttributesCount(int droppedAttributesCount);

    ModifiableAttributes getAttributes();

}
