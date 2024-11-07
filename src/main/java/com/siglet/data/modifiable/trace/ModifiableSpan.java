package com.siglet.data.modifiable.trace;

import com.siglet.data.modifiable.ModifiableAttributes;
import com.siglet.data.modifiable.ModifiableEvents;
import com.siglet.data.modifiable.ModifiableInstrumentationScope;
import com.siglet.data.modifiable.ModifiableResource;
import com.siglet.data.trace.SpanKind;
import com.siglet.data.unmodifiable.trace.UnmodifiableSpan;

public interface ModifiableSpan extends UnmodifiableSpan {


    ModifiableSpan setTraceId(long high, long low);

    ModifiableSpan setTraceId(byte[] traceId);

    ModifiableSpan setSpanId(long spanId);

    ModifiableSpan setParentSpanId(long parentSpanId);

    ModifiableSpan setTraceState(String traceState);

    ModifiableSpan setName(String name);

    ModifiableSpan setStartTimeUnixNano(long startUnixNano);

    ModifiableSpan setEndTimeUnixNano(long endUnixNano);

    ModifiableSpan setKind(SpanKind spanKind);

    ModifiableAttributes getAttributes();

    ModifiableLinks getLinks();

    ModifiableEvents getEvents();

    ModifiableResource getResource();

    ModifiableInstrumentationScope getInstrumentationScope();

    ModifiableSpan setFlags(int flags);

    ModifiableSpan setDroppedAttributesCount(int droppedAttributesCount);

    ModifiableSpan setDroppedEventsCount(int droppedEventsCount);

    ModifiableSpan setDroppedLinksCount(int droppedLinksCount);
}
