package com.siglet.data.unmodifiable.trace;

import com.siglet.data.unmodifiable.trace.UnmodifiableSpan;

public interface UnmodifiableSpanletProcessor<T> {


    void span(UnmodifiableSpan span, T config);

}
