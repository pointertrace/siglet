package com.siglet.data.trace;

public interface UnmodifiableSpanlet {


    void span(UnmodifiableSpan span, UnmodifiableResource resource, UnmodifiableInstrumentationScope instrumentationScope);

}
