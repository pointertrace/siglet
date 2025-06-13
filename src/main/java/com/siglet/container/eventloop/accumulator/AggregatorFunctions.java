package com.siglet.container.eventloop.accumulator;

import com.siglet.SigletError;
import com.siglet.api.Signal;
import com.siglet.container.adapter.metric.ProtoMetricAdapter;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;

import java.util.List;

public class AggregatorFunctions {


    public static ExportTraceServiceRequest aggregateSpans(List<Signal> signals) {
        SignalsAggregator signalsAggregator = new SignalsAggregator();
        signals.forEach(signal -> {
            if (signal instanceof ProtoSpanAdapter protoSpanAdapter) {
                signalsAggregator.add(protoSpanAdapter.getUpdated(), protoSpanAdapter.getUpdatedInstrumentationScope(),
                        protoSpanAdapter.getUpdatedResource());
            } else {
                throw new SigletError(String.format("Can only aggregate spans but signal %s is %s", signal.getId(),
                        signal.getClass().getName()));
            }
        });
        return signalsAggregator.getExportTraceServiceRequestBuilder().build();
    }

    public static ExportMetricsServiceRequest aggregateMetrics(List<Signal> signals) {
        SignalsAggregator signalsAggregator = new SignalsAggregator();
        signals.forEach(signal -> {
            if (signal instanceof ProtoMetricAdapter protoMetricAdapter) {
                signalsAggregator.add(protoMetricAdapter.getUpdated(), protoMetricAdapter.getUpdatedInstrumentationScope(),
                        protoMetricAdapter.getUpdatedResource());
            } else {
                throw new SigletError(String.format("Can only aggregate spans but signal %s is %s", signal.getId(),
                        signal.getClass().getName()));
            }
        });
        return signalsAggregator.getExportMetricsServiceRequestBuilder().build();
    }
}
