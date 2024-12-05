package com.siglet.pipeline.common.processor.groovy.proxy;

import com.siglet.data.adapter.metric.ProtoMetricAdapter;

public class MetricProxy extends BaseProxy{

    protected final ProtoMetricAdapter metricAdapter;

    public MetricProxy(Object thisSignal, ProtoMetricAdapter metricAdapter) {
        super(thisSignal);
        this.metricAdapter = metricAdapter;
    }

    public void name(String name) {
        metricAdapter.setName(name);
    }

    public void description(String description) {
        metricAdapter.setDescription(description);
    }

    public void unit(String unit) {
        metricAdapter.setUnit(unit);
    }

    public ProtoMetricAdapter getMetric() {
        return metricAdapter;
    }

}
