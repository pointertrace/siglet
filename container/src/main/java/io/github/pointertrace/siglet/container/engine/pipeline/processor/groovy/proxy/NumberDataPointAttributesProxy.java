package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.proxy;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.container.adapter.metric.ProtoMetricAdapter;
import io.github.pointertrace.siglet.container.adapter.metric.ProtoNumberDataPointAdapter;

public class NumberDataPointAttributesProxy extends AttributesProxy {

    private final ProtoNumberDataPointAdapter numberDataPointAdapter;

    private final ProtoMetricAdapter metricAdapter;

    public NumberDataPointAttributesProxy(Signal signal, ProtoMetricAdapter metricAdapter,
                                          ProtoNumberDataPointAdapter numberDataPointAdapter) {
        super(signal,numberDataPointAdapter.getAttributes());
        this.metricAdapter = metricAdapter;
        this.numberDataPointAdapter = numberDataPointAdapter;
    }


    public ProtoMetricAdapter getMetric() {
        return metricAdapter;
    }

    public ProtoNumberDataPointAdapter getDataPoint() {
        return numberDataPointAdapter;
    }

}
