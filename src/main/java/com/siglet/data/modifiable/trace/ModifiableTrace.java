package com.siglet.data.modifiable.trace;

import com.siglet.data.unmodifiable.trace.UnmodifiableTrace;

public interface ModifiableTrace<T extends ModifiableSpan> extends UnmodifiableTrace {

    ModifiableTrace<T> add(T span);

    boolean remove(long spanId);

    T get(long spanId);

}
