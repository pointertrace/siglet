package com.siglet.pipeline;

import com.siglet.SigletError;
import com.siglet.data.adapter.metric.ProtoMetricAdapter;
import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import com.siglet.data.adapter.trace.ProtoTraceAdapter;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.trace.v1.Span;
import org.apache.camel.Exchange;

import java.util.Objects;

public interface GroovySignalCreator {

    ProtoMetricAdapter newMetric();

    ProtoSpanAdapter newSpan();

    static GroovySignalCreator create(Exchange exchange) {
        Object body = exchange.getIn().getBody();
        return switch (body) {
            case ProtoMetricAdapter baseMetric -> new MetricBasedGroovySignalCreator(baseMetric);
            case ProtoSpanAdapter baseSpan -> new SpanBasedGroovySignalCreator(baseSpan);
            case ProtoTraceAdapter baseTrace-> {
                    if (baseTrace.getSize() == 0) {
                        throw new SigletError("Cannot create a SignalCreator from a trace without any span");
                    }
                    yield new SpanBasedGroovySignalCreator(baseTrace.getAt(0));
            }
            default ->
                    throw new SigletError("Cannot create a SignalCreator from a body type " + body.getClass().getName());
        };
    }

    class MetricBasedGroovySignalCreator implements GroovySignalCreator {

        private final ProtoMetricAdapter baseMetric;

        public MetricBasedGroovySignalCreator(ProtoMetricAdapter baseMetric) {
            Objects.requireNonNull(baseMetric);
            this.baseMetric = baseMetric;
        }

        @Override
        public ProtoMetricAdapter newMetric() {
            return new ProtoMetricAdapter(Metric.newBuilder().build(),
                    baseMetric.getUpdatedResource(),
                    baseMetric.getUpdatedInstrumentationScope(), true);
        }

        @Override
        public ProtoSpanAdapter newSpan() {
            return new ProtoSpanAdapter(Span.newBuilder().build(),
                    baseMetric.getUpdatedResource(),
                    baseMetric.getUpdatedInstrumentationScope(), true);
        }
    }

    class SpanBasedGroovySignalCreator implements GroovySignalCreator {

        private final ProtoSpanAdapter baseSpan;

        public SpanBasedGroovySignalCreator(ProtoSpanAdapter baseSpan) {
            Objects.requireNonNull(baseSpan);
            this.baseSpan = baseSpan;
        }

        @Override
        public ProtoMetricAdapter newMetric() {
            return new ProtoMetricAdapter(Metric.newBuilder().build(),
                    baseSpan.getUpdatedResource(),
                    baseSpan.getUpdatedInstrumentationScope(), true);
        }

        @Override
        public ProtoSpanAdapter newSpan() {
            return new ProtoSpanAdapter(Span.newBuilder().build(),
                    baseSpan.getUpdatedResource(),
                    baseSpan.getUpdatedInstrumentationScope(), true);
        }
    }
}
