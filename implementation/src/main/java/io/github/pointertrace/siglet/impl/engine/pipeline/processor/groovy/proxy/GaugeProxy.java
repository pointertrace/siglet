package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.proxy;

import groovy.lang.Closure;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.adapter.metric.ProtoMetricAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GaugeProxy extends MetricProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(GaugeProxy.class);

    public GaugeProxy(Signal signal, ProtoMetricAdapter metricAdapter) {
        super(signal, metricAdapter);
        //  TODO check if metric is gauge
    }

    public void dataPoint(Closure<Void> closure) {
        DataPointProxy dataPointProxy = new DataPointProxy(getSignal(), getMetric(),
                getMetric().getGauge().getDataPoints().add(), true);
        closure.setDelegate(dataPointProxy);
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();

    }

}
