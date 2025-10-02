package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy;

import groovy.lang.Closure;
import groovy.lang.Script;
import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.adapter.metric.ProtoMetricAdapter;
import io.github.pointertrace.siglet.impl.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.action.Expression;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.proxy.GaugeProxy;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.proxy.SpanProxy;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.proxy.SumProxy;
import io.github.pointertrace.siglet.impl.eventloop.processor.result.ResultImpl;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.metrics.v1.Gauge;
import io.opentelemetry.proto.metrics.v1.Histogram;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.Sum;
import io.opentelemetry.proto.resource.v1.Resource;

public abstract class ScriptBaseClass extends Script {

    public void drop() {
        BindingUtils.setResult(getBinding(), ResultImpl.drop());
    }

    public void proceed(String destination) {
        BindingUtils.setResult(getBinding(), ResultImpl.proceed(destination));
    }

    public ProtoMetricAdapter newGauge(Closure<Void> closure) {

        Metric gauge = Metric.newBuilder()
                .setGauge(Gauge.newBuilder())
                .build();
        ProtoMetricAdapter newMetric = new ProtoMetricAdapter().recycle(gauge, getResource(),
                getInstrumentationScope());
        GaugeProxy gaugeProxy = new GaugeProxy(BindingUtils.getSignal(getBinding()), newMetric);
        closure.setDelegate(gaugeProxy);
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();
        return newMetric;
    }

    public ProtoMetricAdapter newSum(Closure<Void> closure) {

        Metric sum = Metric.newBuilder()
                .setSum(Sum.newBuilder())
                .build();
        ProtoMetricAdapter newMetric = new ProtoMetricAdapter().recycle(sum, getResource(), getInstrumentationScope());
        newMetric.sum();
        SumProxy sumProxy = new SumProxy(BindingUtils.getSignal(getBinding()), newMetric);
        closure.setDelegate(sumProxy);
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();
        return newMetric;
    }

    public ProtoMetricAdapter newHistogram(Closure<Void> closure) {
        Metric histogram = Metric.newBuilder()
                .setHistogram(Histogram.newBuilder())
                .build();
        ProtoMetricAdapter newMetric = new ProtoMetricAdapter().recycle(histogram, getResource(), getInstrumentationScope());
        newMetric.sum();
        SumProxy sumProxy = new SumProxy(BindingUtils.getSignal(getBinding()), newMetric);
        closure.setDelegate(sumProxy);
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();
        return newMetric;
    }

    public Expression when(Closure<Boolean> closure) {
        return Boolean.TRUE.equals(closure.call()) ? Expression.TRUE_EXPRESSION : Expression.FALSE_EXPRESSION;
    }

    public SignalSender send(Closure<Signal> signalClosure) {
        return new SignalSender(signalClosure.call(),getBinding());
    }

    public void span(Closure<Void> closure) {

        Signal signal = BindingUtils.getSignal(getBinding());
        if (!(signal instanceof ProtoSpanAdapter spanAdapter)) {
            throw new SigletError(String.format("Intrinsic groovy signal %s is not a span!", signal));
        }
        SpanProxy spanProxy = new SpanProxy(spanAdapter, spanAdapter);
        closure.setDelegate(spanProxy);
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();
    }

    private Resource getResource() {

        Signal signal = BindingUtils.getSignal(getBinding());
        switch (signal) {
            case ProtoMetricAdapter metricAdapter -> {
                return metricAdapter.getUpdatedResource();
            }
            case ProtoSpanAdapter spanAdapter -> {
                return spanAdapter.getUpdatedResource();
            }
            case null, default ->
                    throw new SigletError("Cannot get resource from closure variable 'thisSignal' because it is " +
                                          "not a metric, span nor a trace");
        }
    }

    private InstrumentationScope getInstrumentationScope() {
        Signal signal = BindingUtils.getSignal(getBinding());
        switch (signal) {
            case ProtoMetricAdapter metricAdapter -> {
                return metricAdapter.getUpdatedInstrumentationScope();
            }
            case ProtoSpanAdapter spanAdapter -> {
                return spanAdapter.getUpdatedInstrumentationScope();
            }
            case null, default ->
                    throw new SigletError("Cannot get instrumentation scope from closure variable 'thisSignal' " +
                                          "because it is not a metric, span nor a trace");
        }
    }

}
