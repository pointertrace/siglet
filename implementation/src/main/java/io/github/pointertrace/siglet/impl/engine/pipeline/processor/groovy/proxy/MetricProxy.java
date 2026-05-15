package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.proxy;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.adapter.metric.MetricAdapter;

public class MetricProxy extends BaseProxy {

    protected final MetricAdapter metricAdapter;

    public MetricProxy(Signal signal, MetricAdapter metricAdapter) {
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

    public MetricAdapter getMetric() {
        return metricAdapter;
    }

}
