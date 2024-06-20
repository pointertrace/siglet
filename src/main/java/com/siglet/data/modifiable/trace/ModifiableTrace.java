package com.siglet.data.modifiable.trace;

import com.siglet.data.modifiable.trace.ModifiableSpan;
import com.siglet.data.unmodifiable.trace.UnmodifiableTrace;

public interface ModifiableTrace extends UnmodifiableTrace {

    void add(ModifiableSpan span);

    boolean remove(long spanId);

    ModifiableSpan get(long spanId);

}
