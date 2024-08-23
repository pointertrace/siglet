package com.siglet.data.modifiable.metric;

import com.siglet.data.modifiable.ModifiableAttributes;
import com.siglet.data.unmodifiable.metric.UnmodifiableNumberDataPoint;

public interface ModifiableNumberDataPoint extends UnmodifiableNumberDataPoint {


    ModifiableAttributes getAttributes();

    ModifiableExemplars getExemplars();

    ModifiableNumberDataPoint setStartTimeUnixNano(long startTimeUnixNano);

    ModifiableNumberDataPoint setTimeUnixNano(long timeUnixNano);

    ModifiableNumberDataPoint setAsLong(long value);

    ModifiableNumberDataPoint setAsDouble(double value);

    long getAsLong();

    double getAsDouble();

    ModifiableNumberDataPoint setFlags(int flags);

}
