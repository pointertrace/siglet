package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.proxy;

import groovy.lang.Closure;
import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.adapter.metric.MetricAdapter;
import io.github.pointertrace.siglet.impl.adapter.metric.NumberDataPointAdapter;

public class DataPointProxy extends BaseProxy {

    private final NumberDataPointAdapter dataPointAdapter;
    private final MetricAdapter metricAdapter;

    private final boolean allowDoubleAsValue;

    public DataPointProxy(Signal signal, MetricAdapter metricAdapter,
                          NumberDataPointAdapter dataPointAdapter,
                          boolean allowDoubleAsValue) {
        super(signal);
        this.metricAdapter = metricAdapter;
        this.dataPointAdapter = dataPointAdapter;
        this.allowDoubleAsValue = allowDoubleAsValue;
    }

    public void startTimeUnixNano(long startTimeUnixNano) {
        dataPointAdapter.setStartTimeUnixNano(startTimeUnixNano);
    }

    public void timeUnixNano(long timeUnixNano) {
        dataPointAdapter.setTimeUnixNano(timeUnixNano);
    }

    public void value(long value) {
        dataPointAdapter.setAsLong(value);
    }

    public void value(double value) {
        if (!allowDoubleAsValue) {
            throw new IllegalStateException("Value is not allowed as double");
        }
        dataPointAdapter.setAsDouble(value);
    }

    public Object getValue() {
        if (dataPointAdapter.hasDoubleValue()) {
            return dataPointAdapter.getAsDouble();
        }
        if (dataPointAdapter.hasLongValue()) {
            return dataPointAdapter.getAsLong();
        } else {
            throw new SigletError("Data point has no long or double value");
        }
    }

    public void flags(int flags) {
        dataPointAdapter.setFlags(flags);
    }

    public void attributes(Closure<Void> closure) {
        closure.setDelegate(new NumberDataPointAttributesProxy(getSignal(), metricAdapter, dataPointAdapter));
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();
    }

    // TODO incluir exemplars


}
