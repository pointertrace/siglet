package com.siglet.container.engine.pipeline.processor.groovy.proxy;

import com.siglet.api.Signal;
import com.siglet.container.adapter.metric.ProtoMetricAdapter;
import groovy.lang.Closure;

public class CounterProxy extends MetricProxy {

    public CounterProxy(Signal signal, ProtoMetricAdapter metricAdapter) {
        super(signal,metricAdapter);
        //  TODO check if metric is counter
    }

    public void dataPoint(Closure<Void> closure) {
        DataPointProxy dataPointProxy = new DataPointProxy(getSignal(),getMetric(),
                getMetric().getSum().getDataPoints().add(), false);
        closure.setDelegate(dataPointProxy);
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();

    }


}
