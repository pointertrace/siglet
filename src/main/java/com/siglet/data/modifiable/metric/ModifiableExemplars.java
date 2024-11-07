package com.siglet.data.modifiable.metric;

import com.siglet.data.unmodifiable.metric.UnmodifiableExemplars;

public interface ModifiableExemplars extends UnmodifiableExemplars {

    ModifiableExemplar get(int i);

    void remove(int i);

}
