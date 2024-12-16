package com.siglet.pipeline.common.processor.groovy;

import com.siglet.SigletError;
import com.siglet.cli.SigletContext;
import com.siglet.data.adapter.metric.ProtoMetricAdapter;
import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import com.siglet.data.adapter.trace.ProtoTrace;
import com.siglet.pipeline.common.processor.groovy.proxy.CounterProxy;
import com.siglet.pipeline.common.processor.groovy.proxy.GaugeProxy;
import com.siglet.pipeline.common.processor.groovy.proxy.SpanProxy;
import groovy.lang.Closure;
import groovy.lang.Script;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import org.apache.camel.CamelContext;

public abstract class ScriptBaseClass extends Script {

    public static final String SIGNAL_INTRINSIC_VAR_NAME = "thisSignal";

    public ProtoMetricAdapter newGauge(Closure<Void> closure) {

        ProtoMetricAdapter newMetric = new ProtoMetricAdapter(getResource(), getInstrumentationScope());
        newMetric.gauge();
        GaugeProxy gaugeProxy = new GaugeProxy(getBinding().getProperty(SIGNAL_INTRINSIC_VAR_NAME), newMetric);
        closure.setDelegate(gaugeProxy);
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();
        return newMetric;
    }

    public ProtoMetricAdapter newCounter(Closure<Void> closure) {

        ProtoMetricAdapter newMetric = new ProtoMetricAdapter(getResource(), getInstrumentationScope());
        newMetric.sum();
        CounterProxy counterProxy = new CounterProxy(getBinding().getProperty(SIGNAL_INTRINSIC_VAR_NAME), newMetric);
        closure.setDelegate(counterProxy);
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();
        return newMetric;
    }

    public Expression when(Closure<Boolean> closure) {
        return closure.call() ? Expression.TRUE_EXPRESSION : Expression.FALSE_EXPRESSION;
    }

    public SignalSender to(String destination) {
        return new SignalSender(destination);
    }

    public CamelContext getContext() {
        return SigletContext.getInstance().getContextSupplier().get();
    }

    public void span(Closure<Void> closure) {

        if (!getBinding().hasVariable(SIGNAL_INTRINSIC_VAR_NAME)) {
            throw new SigletError("Could not find thisSignal property!");
        }
        if (!(getBinding().getProperty(SIGNAL_INTRINSIC_VAR_NAME) instanceof ProtoSpanAdapter spanAdapter)) {
            throw new SigletError("Property thisSignal is not a span!");
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
