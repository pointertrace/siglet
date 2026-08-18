package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.proxy;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.adapter.metric.MetricAdapter;
import io.github.pointertrace.siglet.impl.adapter.metric.NumberDataPointAdapter;

public class NumberDataPointAttributesProxy extends AttributesProxy {

    private final NumberDataPointAdapter numberDataPointAdapter;

    private final MetricAdapter metricAdapter;

    public NumberDataPointAttributesProxy(Signal signal, MetricAdapter metricAdapter,
                                          NumberDataPointAdapter numberDataPointAdapter) {
        super(signal,numberDataPointAdapter.getAttributes());
        this.metricAdapter = metricAdapter;
        this.numberDataPointAdapter = numberDataPointAdapter;
    }


    public MetricAdapter getMetric() {
        return metricAdapter;
    }

    public NumberDataPointAdapter getDataPoint() {
        return numberDataPointAdapter;
    }

}
