package com.siglet.data.modifiable;

import com.siglet.data.unmodifiable.UnmodifiableTrace;

public interface ModifiableTrace extends UnmodifiableTrace {

    void add(ModifiableSpan span);

    boolean remove(long spanId);

    ModifiableSpan get(long spanId);

}
