package com.siglet.container.engine.pipeline.accumulator;

import com.siglet.SigletError;
import com.siglet.api.Signal;
import com.siglet.container.adapter.metric.ProtoMetricAdapter;

import java.util.List;

public class MetricAccumulator {


    public static AccumulatedMetrics accumulate(List<Signal> signals) {
        SignalsAccumulator signalsAccumulator = new SignalsAccumulator();
        StringBuilder sb = new StringBuilder("Aggregated Metrics[");
        signals.forEach(signal -> {
            if (signal instanceof ProtoMetricAdapter protoMetricAdapter) {
                sb.append(protoMetricAdapter.getId());
//                signalsAggregator.add(protoMetricAdapter.getUpdated(), protoMetricAdapter.getUpdatedInstrumentationScope(),
//                        protoMetricAdapter.getUpdatedResource());
            } else {
                throw new SigletError(String.format("Can only aggregate spans but signal %s is %s", signal.getId(),
                        signal.getClass().getName()));
            }
        });
        sb.append("]");
        return new AccumulatedMetrics(signalsAccumulator.getExportMetricsServiceRequestBuilder().build(), sb.toString());
    }
}
