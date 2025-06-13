package com.siglet.container.engine.pipeline.processor.groovy.proxy;

import com.siglet.api.Signal;
import com.siglet.container.adapter.metric.ProtoMetricAdapter;
import com.siglet.container.adapter.metric.ProtoNumberDataPointAdapter;

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
