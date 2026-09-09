package io.github.pointertrace.siglet.impl.engine.exporter.grpc.accumulator.metric;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.adapter.metric.MetricAdapter;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.opentelemetry.proto.metrics.v1.MetricsData;
import io.opentelemetry.sdk.metrics.data.MetricData;

public class MetricAccumulator {

    private MetricAccumulator() {
    }


    public static AccumulatedMetrics accumulateMetrics(Object[] metrics) {
        MetricsAccumulator metricsAccumulator = new MetricsAccumulator();
        for (Object metric : metrics) {
            if (metric instanceof MetricAdapter protoMetricAdapter) {
                metricsAccumulator.add(protoMetricAdapter.getUpdated(),
                        protoMetricAdapter.getUpdatedScope(),
                        protoMetricAdapter.getUpdatedResource());
            } else if (metric instanceof MetricData metricData) {
                metricsAccumulator.add(metricData);
            } else {
                throw new SigletError(String.format("Can only aggregate metrics but signal is %s", metric.getClass().getName()));
            }
        }
        return new AccumulatedMetrics(metricsAccumulator.getExportMetricsServiceRequest(),metricsAccumulator.getNumSignals());
    }
}
