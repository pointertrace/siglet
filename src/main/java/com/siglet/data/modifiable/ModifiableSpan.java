package com.siglet.data.modifiable;

import com.google.protobuf.ByteString;
import com.siglet.data.trace.SpanKind;
import com.siglet.data.unmodifiable.UnmodifiableSpan;

import java.nio.ByteBuffer;

public interface ModifiableSpan extends UnmodifiableSpan {


    void setTraceId(long high, long low);

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

    void setFlags(int flags);

    void setDroppedAttributesCount(int droppedAttributesCount);

    void setDroppedEventsCount(int droppedEventsCount);

    void setDroppedLinksCount(int droppedLinksCount);
}
