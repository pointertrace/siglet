package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.proxy;

import groovy.lang.Closure;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.adapter.metric.ProtoMetricAdapter;

public class HistogramProxy extends MetricProxy {

    public HistogramProxy(Signal signal, ProtoMetricAdapter metricAdapter) {
        super(signal, metricAdapter);
        //  TODO check if metric is histogram
    }

    public void dataPoint(Closure<Void> closure) {
        DataPointProxy dataPointProxy = new DataPointProxy(getSignal(), getMetric(),
                getMetric().getGauge().getDataPoints().add(), true);
        closure.setDelegate(dataPointProxy);
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();

    }

}
