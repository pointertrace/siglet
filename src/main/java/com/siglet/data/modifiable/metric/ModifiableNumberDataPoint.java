package com.siglet.data.modifiable.metric;

import com.siglet.data.modifiable.ModifiableAttributes;
import com.siglet.data.unmodifiable.metric.UnmodifiableNumberDataPoint;

public interface ModifiableNumberDataPoint extends UnmodifiableNumberDataPoint {


    ModifiableAttributes getAttributes();


    ModifiableExemplars getExemplars();

    void setStartTimeUnixNano(long startTimeUnixNano);

    void setTimeUnixNano(long timeUnixNano);

    long getAsLong();

    double getAsDouble();

    void setFlags(int flags);
}
