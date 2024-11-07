package com.siglet.pipeline.common.processor.groovy.proxy;

import com.siglet.SigletError;
import com.siglet.data.adapter.metric.ProtoMetricAdapter;
import com.siglet.data.adapter.metric.ProtoNumberDataPointAdapter;
import groovy.lang.Closure;

public class DataPointProxy extends BaseProxy {

    private final ProtoNumberDataPointAdapter dataPointAdapter;
    private final ProtoMetricAdapter metricAdapter;

    private final boolean allowDoubleAsValue;

    public DataPointProxy(Object thisSignal, ProtoMetricAdapter metricAdapter, ProtoNumberDataPointAdapter dataPointAdapter,
                          boolean allowDoubleAsValue) {
        super(thisSignal);
        this.metricAdapter = metricAdapter;
        this.dataPointAdapter = dataPointAdapter;
        this.allowDoubleAsValue = allowDoubleAsValue;
    }

    public void startTimeUnixNano(long startTimeUnixNano) {
        dataPointAdapter.setStartTimeUnixNano(startTimeUnixNano);
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

    public Object getValue(){
        if (dataPointAdapter.hasDoubleValue()) {
            return dataPointAdapter.getAsDouble();
        } if (dataPointAdapter.hasLongValue()) {
            return dataPointAdapter.getAsLong();
        } else {
            throw new SigletError("Data point has no long or double value");
        }
    }

    public void flags(int flags) {
        dataPointAdapter.setFlags(flags);
    }

    public void attributes(Closure<Void> closure) {
        System.out.println(closure);
        closure.setDelegate(new NumberDataPointAttributesProxy(getThisSignal(), metricAdapter, dataPointAdapter));
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();
    }

    // TODO incluir exemplars


}
