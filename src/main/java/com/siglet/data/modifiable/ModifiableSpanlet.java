package com.siglet.data.modifiable;

import com.siglet.data.unmodifiable.UnmodifiableInstrumentationScope;
import com.siglet.data.unmodifiable.UnmodifiableResource;
import com.siglet.data.unmodifiable.UnmodifiableSpan;

public interface ModifiableSpanlet {


    void span(ModifiableSpan span);

}
