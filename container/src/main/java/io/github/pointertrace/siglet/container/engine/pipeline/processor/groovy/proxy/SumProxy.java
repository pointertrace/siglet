package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.proxy;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.api.signal.metric.AggregationTemporality;
import io.github.pointertrace.siglet.container.adapter.metric.ProtoMetricAdapter;
import groovy.lang.Closure;

public class SumProxy extends MetricProxy {

    public SumProxy(Signal signal, ProtoMetricAdapter metricAdapter) {
        super(signal,metricAdapter);
    }

    public void dataPoint(Closure<Void> closure) {
        DataPointProxy dataPointProxy = new DataPointProxy(getSignal(),getMetric(),
                getMetric().getSum().getDataPoints().add(), false);
        closure.setDelegate(dataPointProxy);
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();
    }

    public void monotonic(boolean monotonic) {
        getMetric().getSum().setMonotonic(monotonic);
    }

    public void aggregationTemporality(AggregationTemporality aggregationTemporality) {
        getMetric().getSum().setAggregationTemporality(aggregationTemporality);
    }

    public AggregationTemporality getDELTA() {
        return AggregationTemporality.DELTA;
    }

    public AggregationTemporality getCUMULATIVE() {
        return AggregationTemporality.CUMULATIVE;
    }

}
