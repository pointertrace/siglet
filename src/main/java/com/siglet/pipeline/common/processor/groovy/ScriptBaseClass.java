package com.siglet.pipeline.common.processor.groovy;

import com.siglet.SigletError;
import com.siglet.cli.SigletContext;
import com.siglet.data.adapter.metric.ProtoMetricAdapter;
import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import com.siglet.data.adapter.trace.ProtoTraceAdapter;
import com.siglet.pipeline.common.processor.groovy.proxy.CounterProxy;
import com.siglet.pipeline.common.processor.groovy.proxy.GaugeProxy;
import com.siglet.pipeline.common.processor.groovy.proxy.SpanProxy;
import groovy.lang.Closure;
import groovy.lang.Script;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

import java.util.function.Supplier;

public abstract class ScriptBaseClass extends Script {

    public ProtoMetricAdapter newGauge(Closure<Void> closure) {

        ProtoMetricAdapter newMetric = new ProtoMetricAdapter(getResource(), getInstrumentationScope());
        newMetric.gauge();
        GaugeProxy gaugeProxy = new GaugeProxy(getBinding().getProperty("thisSignal"), newMetric);
        closure.setDelegate(gaugeProxy);
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();
        return newMetric;
    }

    public ProtoMetricAdapter newCounter(Closure<Void> closure) {

        ProtoMetricAdapter newMetric = new ProtoMetricAdapter(getResource(), getInstrumentationScope());
        newMetric.sum();
        CounterProxy counterProxy = new CounterProxy(getBinding().getProperty("thisSignal"), newMetric);
        closure.setDelegate(counterProxy);
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();
        return newMetric;
    }

    public Expression when(Closure<Boolean> closure) {
        return closure.call()? Expression.TRUE_EXPRESSION: Expression.FALSE_EXPRESSION;
    }

    public SignalSender to(String destination) {
        return new SignalSender(destination);
    }

    public CamelContext getContext() {
        return SigletContext.getInstance().getContextSupplier().get();
    }

    public void span(Closure<Void> closure) {

        if (!getBinding().hasVariable("thisSignal")) {
            throw new SigletError("Could not find thisSignal property!");
        }
        if (!(getBinding().getProperty("thisSignal") instanceof ProtoSpanAdapter spanAdapter)) {
            throw new SigletError("Property thisSignal is not a span!");
        }
        SpanProxy spanProxy = new SpanProxy(spanAdapter, spanAdapter);
        closure.setDelegate(spanProxy);
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();
    }

    private Resource getResource() {
        if (getBinding().hasVariable("thisSignal")) {
            Object closureThis = getBinding().getProperty("thisSignal");
            switch (closureThis) {
                case ProtoMetricAdapter metricAdapter -> {
                    return metricAdapter.getUpdatedResource();
                }
                case ProtoSpanAdapter spanAdapter -> {
                    return spanAdapter.getUpdatedResource();
                }
                case ProtoTraceAdapter traceAdapter -> {
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
        if (getBinding().hasVariable("thisSignal")) {
            Object closureThis = getBinding().getProperty("thisSignal");
            switch (closureThis) {
                case ProtoMetricAdapter metricAdapter -> {
                    return metricAdapter.getUpdatedInstrumentationScope();
                }
                case ProtoSpanAdapter spanAdapter -> {
                    return spanAdapter.getUpdatedInstrumentationScope();
                }
                case ProtoTraceAdapter traceAdapter -> {
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
