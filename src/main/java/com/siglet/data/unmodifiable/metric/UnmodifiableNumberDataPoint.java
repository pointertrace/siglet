package com.siglet.data.unmodifiable.metric;

import com.siglet.data.unmodifiable.UnmodifiableAttributes;

public interface UnmodifiableNumberDataPoint {


    UnmodifiableAttributes getAttributes();

    long getStartTimeUnixNano();

    long getTimeUnixNano();

    void setAsLong(long value);

    void setAsDouble(double value);

    int getFlags();

}
