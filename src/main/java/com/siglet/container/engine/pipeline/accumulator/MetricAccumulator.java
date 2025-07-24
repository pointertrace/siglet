package com.siglet.container.engine.pipeline.accumulator;

import com.siglet.SigletError;
import com.siglet.api.Signal;
import com.siglet.container.adapter.metric.ProtoMetricAdapter;
import com.siglet.container.engine.Context;

import java.util.List;

public class MetricAccumulator {

    private MetricAccumulator() {
    }


    public static AccumulatedMetrics accumulateMetrics(Context context, List<Signal> signals) {
        MetricsAccumulator metricsAccumulator = new MetricsAccumulator();
        StringBuilder sb = new StringBuilder("Aggregated Metrics[");
        signals.forEach(signal -> {
            if (signal instanceof ProtoMetricAdapter protoMetricAdapter) {
                sb.append(protoMetricAdapter.getId());
                metricsAccumulator.add(protoMetricAdapter.getUpdated(),
                        protoMetricAdapter.getUpdatedInstrumentationScope(),
                        protoMetricAdapter.getUpdatedResource());
                context.getMetricObjectPool().recycle(protoMetricAdapter);
            } else {
                throw new SigletError(String.format("Can only aggregate spans but signal %s is %s", signal.getId(),
                        signal.getClass().getName()));
            }
        });
        sb.append("]");
        return new AccumulatedMetrics(metricsAccumulator.getExportMetricsServiceRequest(), sb.toString());
    }
}
