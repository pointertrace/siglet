package com.siglet.pipeline.common.processor.groovy.proxy;

import com.siglet.data.adapter.metric.ProtoMetricAdapter;
import groovy.lang.Closure;

public class GaugeProxy extends MetricProxy {

    public GaugeProxy(Object thisSignal, ProtoMetricAdapter metricAdapter) {
        super(thisSignal, metricAdapter);
        //  TODO check if metric is gauge
    }

    public void dataPoint(Closure<Void> closure) {
        DataPointProxy dataPointProxy = new DataPointProxy(getThisSignal(), getMetric(),
                getMetric().getGauge().getDataPoints().add(), true);
        closure.setDelegate(dataPointProxy);
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();

    }

}
