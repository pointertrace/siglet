package io.github.pointertrace.siglet.api.signal.metric;

import io.github.pointertrace.siglet.api.signal.Attributes;

public interface NumberDataPoint {

    Attributes getAttributes();

    Exemplars getExemplars();

    long getStartTimeUnixNano();

    NumberDataPoint setStartTimeUnixNano(long startTimeUnixNano);

    long getTimeUnixNano();

    NumberDataPoint setTimeUnixNano(long timeUnixNano);

    boolean hasLongValue();

    long getAsLong();

    NumberDataPoint setAsLong(long value);

    boolean hasDoubleValue();

    double getAsDouble();

    NumberDataPoint setAsDouble(double value);

    Object getValue();

    int getFlags();

    NumberDataPoint setFlags(int flags);

}
