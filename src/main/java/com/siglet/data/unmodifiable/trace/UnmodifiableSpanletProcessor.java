package com.siglet.data.unmodifiable.trace;

public interface UnmodifiableSpanletProcessor<T> {


    void span(UnmodifiableSpan span, T config);

}
