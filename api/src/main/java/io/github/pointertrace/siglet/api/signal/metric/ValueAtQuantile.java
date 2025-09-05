package io.github.pointertrace.siglet.api.signal.metric;

public interface ValueAtQuantile {

    double getQuantile();

    ValueAtQuantile setQuantile(double quantile);

    double getValue();

    ValueAtQuantile setValue(double value);
}
