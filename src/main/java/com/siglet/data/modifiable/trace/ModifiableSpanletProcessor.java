package com.siglet.data.modifiable.trace;

public interface ModifiableSpanletProcessor<T> {


    void span(ModifiableSpan span, T config);

}
