package com.siglet.data.modifiable.trace;

import com.siglet.data.modifiable.trace.ModifiableSpan;

public interface ModifiableSpanletProcessor<T> {


    void span(ModifiableSpan span, T config);

}
