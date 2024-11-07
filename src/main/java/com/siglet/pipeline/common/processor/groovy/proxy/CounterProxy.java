package com.siglet.pipeline.common.processor.groovy.proxy;

import com.siglet.data.adapter.metric.ProtoMetricAdapter;
import groovy.lang.Closure;

public class CounterProxy extends MetricProxy {

    public CounterProxy(Object thisSignal, ProtoMetricAdapter metricAdapter) {
        super(thisSignal,metricAdapter);
        //  TODO check if metric is counter
    }

    public void dataPoint(Closure<Void> closure) {
        DataPointProxy dataPointProxy = new DataPointProxy(getThisSignal(),getMetric(),
                getMetric().getSum().getDataPoints().add(), false);
        closure.setDelegate(dataPointProxy);
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();

    }


}
