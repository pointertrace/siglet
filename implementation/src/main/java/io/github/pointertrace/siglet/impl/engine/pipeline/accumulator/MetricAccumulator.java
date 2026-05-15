package io.github.pointertrace.siglet.impl.engine.pipeline.accumulator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.adapter.metric.MetricAdapter;
import io.github.pointertrace.siglet.impl.engine.SigletContext;

import java.util.List;

public class MetricAccumulator {

    private MetricAccumulator() {
    }


    public static AccumulatedMetrics accumulateMetrics(SigletContext sigletContext, Signal[] signals) {
        MetricsAccumulator metricsAccumulator = new MetricsAccumulator();
        StringBuilder sb = new StringBuilder("Aggregated Metrics[");
        for (Signal signal : signals) {
            if (signal instanceof MetricAdapter protoMetricAdapter) {
                sb.append(protoMetricAdapter.getId());
                metricsAccumulator.add(protoMetricAdapter.getUpdated(),
                        protoMetricAdapter.getUpdatedScope(),
                        protoMetricAdapter.getUpdatedResource());
            } else {
                throw new SigletError(String.format("Can only aggregate spans but signal %s is %s", signal.getId(),
                        signal.getClass().getName()));
            }
        }
        sb.append("]");
        return new AccumulatedMetrics(metricsAccumulator.getExportMetricsServiceRequest(), sb.toString());
    }
}
