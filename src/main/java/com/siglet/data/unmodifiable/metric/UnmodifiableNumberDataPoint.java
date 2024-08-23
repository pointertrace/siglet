package com.siglet.data.unmodifiable.metric;

import com.siglet.data.unmodifiable.UnmodifiableAttributes;

public interface UnmodifiableNumberDataPoint {

    UnmodifiableAttributes getAttributes();

    UnmodifiableExemplars getExemplars();

    long getStartTimeUnixNano();

    long getTimeUnixNano();

    int getFlags();


}
