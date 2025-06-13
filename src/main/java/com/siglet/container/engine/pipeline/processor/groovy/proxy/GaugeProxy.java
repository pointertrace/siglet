package com.siglet.container.engine.pipeline.processor.groovy.proxy;

import com.siglet.api.Signal;
import com.siglet.container.adapter.metric.ProtoMetricAdapter;
import groovy.lang.Closure;

public class GaugeProxy extends MetricProxy {

    public GaugeProxy(Signal signal, ProtoMetricAdapter metricAdapter) {
        super(signal, metricAdapter);
        //  TODO check if metric is gauge
    }

    public void dataPoint(Closure<Void> closure) {
        DataPointProxy dataPointProxy = new DataPointProxy(getSignal(), getMetric(),
                getMetric().getGauge().getDataPoints().add(), true);
        closure.setDelegate(dataPointProxy);
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();

    }

}
