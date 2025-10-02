package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.proxy;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.adapter.metric.ProtoMetricAdapter;

public class MetricProxy extends BaseProxy {

    protected final ProtoMetricAdapter metricAdapter;

    public MetricProxy(Signal signal, ProtoMetricAdapter metricAdapter) {
        super(signal);
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
