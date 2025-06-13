package com.siglet.container.engine.pipeline.processor.groovy;

import com.siglet.SigletError;
import com.siglet.api.Signal;
import com.siglet.container.adapter.metric.ProtoMetricAdapter;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.adapter.trace.ProtoTrace;
import com.siglet.container.engine.pipeline.processor.groovy.action.Expression;
import com.siglet.container.engine.pipeline.processor.groovy.proxy.CounterProxy;
import com.siglet.container.engine.pipeline.processor.groovy.proxy.GaugeProxy;
import com.siglet.container.engine.pipeline.processor.groovy.proxy.SpanProxy;
import com.siglet.container.eventloop.processor.result.ResultImpl;
import groovy.lang.Closure;
import groovy.lang.Script;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;

public abstract class ScriptBaseClass extends Script {

    public static final String SIGNAL_INTRINSIC_VAR_NAME = "signal";

    public ProtoMetricAdapter newGauge(Closure<Void> closure) {

        ProtoMetricAdapter newMetric = new ProtoMetricAdapter(getResource(), getInstrumentationScope());
        newMetric.gauge();
        GaugeProxy gaugeProxy = new GaugeProxy((Signal) getBinding().getProperty(SIGNAL_INTRINSIC_VAR_NAME), newMetric);
        closure.setDelegate(gaugeProxy);
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();
        return newMetric;
    }

    public void drop() {
        getBinding().setProperty("result", ResultImpl.drop());
    }

    public void proceed(String destination) {
        getBinding().setProperty("result", ResultImpl.proceed(destination));
    }

    public ProtoMetricAdapter newCounter(Closure<Void> closure) {

        ProtoMetricAdapter newMetric = new ProtoMetricAdapter(getResource(), getInstrumentationScope());
        newMetric.sum();
        CounterProxy counterProxy = new CounterProxy((Signal) getBinding().getProperty(SIGNAL_INTRINSIC_VAR_NAME), newMetric);
        closure.setDelegate(counterProxy);
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();
        return newMetric;
    }

    public Expression when(Closure<Boolean> closure) {
        return Boolean.TRUE.equals(closure.call()) ? Expression.TRUE_EXPRESSION : Expression.FALSE_EXPRESSION;
    }

    // TODO acertar to
//    public SignalSender to(String destination) {
//        return new SignalSender(destination);
//    }

    public void span(Closure<Void> closure) {

        if (!getBinding().hasVariable(SIGNAL_INTRINSIC_VAR_NAME)) {
            throw new SigletError(String.format("Could not find %s property!",SIGNAL_INTRINSIC_VAR_NAME));
        }
        if (!(getBinding().getProperty(SIGNAL_INTRINSIC_VAR_NAME) instanceof ProtoSpanAdapter spanAdapter)) {
            throw new SigletError(String.format("Property %s is not a span!",SIGNAL_INTRINSIC_VAR_NAME));
        }
        SpanProxy spanProxy = new SpanProxy(spanAdapter, spanAdapter);
        closure.setDelegate(spanProxy);
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();
    }

    private Resource getResource() {
        if (getBinding().hasVariable(SIGNAL_INTRINSIC_VAR_NAME)) {
            Object closureThis = getBinding().getProperty(SIGNAL_INTRINSIC_VAR_NAME);
            switch (closureThis) {
                case ProtoMetricAdapter metricAdapter -> {
                    return metricAdapter.getUpdatedResource();
                }
                case ProtoSpanAdapter spanAdapter -> {
                    return spanAdapter.getUpdatedResource();
                }
                case ProtoTrace traceAdapter -> {
                    if (traceAdapter.getSize() == 0) {
                        throw new SigletError("Cannot get resource from a trace without spans");
                    } else {
                        return traceAdapter.getAt(0).getUpdatedResource();
                    }
                }
                case null, default ->
                        throw new SigletError("Cannot get resource from closure variable 'thisSignal' because it is " +
                                "not a metric, span nor a trace");
            }
        } else {
            throw new SigletError("Cannot get resource because there is no 'thisSignal' variable this from closure");
        }
    }

    private InstrumentationScope getInstrumentationScope() {
        if (getBinding().hasVariable(SIGNAL_INTRINSIC_VAR_NAME)) {
            Object closureThis = getBinding().getProperty(SIGNAL_INTRINSIC_VAR_NAME);
            switch (closureThis) {
                case ProtoMetricAdapter metricAdapter -> {
                    return metricAdapter.getUpdatedInstrumentationScope();
                }
                case ProtoSpanAdapter spanAdapter -> {
                    return spanAdapter.getUpdatedInstrumentationScope();
                }
                case ProtoTrace traceAdapter -> {
                    if (traceAdapter.getSize() == 0) {
                        throw new SigletError("Cannot get resource from a trace without spans");
                    } else {
                        return traceAdapter.getAt(0).getUpdatedInstrumentationScope();
                    }
                }
                case null, default ->
                        throw new SigletError("Cannot get instrumentation scope from closure variable 'thisSignal' " +
                                "because it is not a metric, span nor a trace");
            }
        } else {
            throw new SigletError("Cannot get instrumentation scope because there is no 'thisSignal' variable this" +
                    " from closure");
        }
    }

}
