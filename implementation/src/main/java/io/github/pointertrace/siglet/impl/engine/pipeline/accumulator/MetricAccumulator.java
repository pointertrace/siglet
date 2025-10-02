package io.github.pointertrace.siglet.impl.engine.pipeline.accumulator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.adapter.metric.ProtoMetricAdapter;
import io.github.pointertrace.siglet.impl.engine.Context;

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
