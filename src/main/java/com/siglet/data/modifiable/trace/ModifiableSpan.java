package com.siglet.data.modifiable.trace;

import com.siglet.data.modifiable.ModifiableAttributes;
import com.siglet.data.modifiable.ModifiableEvents;
import com.siglet.data.modifiable.ModifiableInstrumentationScope;
import com.siglet.data.modifiable.ModifiableResource;
import com.siglet.data.modifiable.trace.ModifiableLinks;
import com.siglet.data.trace.SpanKind;
import com.siglet.data.unmodifiable.trace.UnmodifiableSpan;

public interface ModifiableSpan extends UnmodifiableSpan {


    void setTraceId(long high, long low);

    void setTraceId(byte[] traceId);

    void setSpanId(long spanId);

    void setParentSpanId(long parentSpanId);

    void setTraceState(String traceState);

    void setName(String name);

    void setStartTimeUnixNano(long startUnixNano);

    void setEndTimeUnixNano(long endUnixNano);

    void setKind(SpanKind spanKind);

    ModifiableAttributes getAttributes();

    ModifiableLinks getLinks();

    ModifiableEvents getEvents();

    ModifiableResource getResource();

    ModifiableInstrumentationScope getInstrumentationScope();

    void setFlags(int flags);

    void setDroppedAttributesCount(int droppedAttributesCount);

    void setDroppedEventsCount(int droppedEventsCount);

    void setDroppedLinksCount(int droppedLinksCount);
}
