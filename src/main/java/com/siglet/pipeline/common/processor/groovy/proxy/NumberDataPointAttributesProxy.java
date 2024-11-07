package com.siglet.pipeline.common.processor.groovy.proxy;

import com.siglet.data.adapter.metric.ProtoMetricAdapter;
import com.siglet.data.adapter.metric.ProtoNumberDataPointAdapter;

public class NumberDataPointAttributesProxy extends AttributesProxy {

    private final ProtoNumberDataPointAdapter numberDataPointAdapter;

    private final ProtoMetricAdapter metricAdapter;

    public NumberDataPointAttributesProxy(Object thisSignal, ProtoMetricAdapter metricAdapter,ProtoNumberDataPointAdapter numberDataPointAdapter) {
        super(thisSignal,numberDataPointAdapter.getAttributes());
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
