package com.siglet.data.modifiable;

import com.siglet.data.unmodifiable.UnmodifiableSpan;

public interface ModifiableTrace extends UnmodifiableSpan {

    void add(ModifiableSpan span);

    void remove(long spanId);

    ModifiableSpan get(long spanId);

}
