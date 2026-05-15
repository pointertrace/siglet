package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.AggregationTemporality;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetricAdapterTest {

    @Test
    void shouldKeepSameProtoInstanceWhenUnchanged() {
        io.opentelemetry.proto.metrics.v1.Metric metric = io.opentelemetry.proto.metrics.v1.Metric.newBuilder().setName("cpu").build();
        io.opentelemetry.proto.resource.v1.Resource resource = io.opentelemetry.proto.resource.v1.Resource.getDefaultInstance();
        io.opentelemetry.proto.common.v1.InstrumentationScope scope = io.opentelemetry.proto.common.v1.InstrumentationScope.getDefaultInstance();

        MetricAdapter adapter = new MetricAdapter(metric, resource, scope);

        assertSame(metric, adapter.getUpdated());
        assertSame(resource, adapter.getUpdatedResource());
        assertSame(scope, adapter.getUpdatedScope());
    }

    @Test
    void shouldReturnSameContainedGaugeProtoInstance() {
        io.opentelemetry.proto.metrics.v1.Gauge gauge = io.opentelemetry.proto.metrics.v1.Gauge.getDefaultInstance();
        io.opentelemetry.proto.metrics.v1.Metric metric = io.opentelemetry.proto.metrics.v1.Metric.newBuilder()
            .setName("cpu").setGauge(gauge).build();
        MetricAdapter adapter = new MetricAdapter(metric,
            io.opentelemetry.proto.resource.v1.Resource.getDefaultInstance(),
            io.opentelemetry.proto.common.v1.InstrumentationScope.getDefaultInstance());

        assertSame(gauge, adapter.getUpdated().getGauge());
    }

    @Test
    void shouldReturnSameContainedSumProtoInstance() {
        io.opentelemetry.proto.metrics.v1.Sum sum = io.opentelemetry.proto.metrics.v1.Sum.getDefaultInstance();
        io.opentelemetry.proto.metrics.v1.Metric metric = io.opentelemetry.proto.metrics.v1.Metric.newBuilder()
            .setName("requests").setSum(sum).build();
        MetricAdapter adapter = new MetricAdapter(metric,
            io.opentelemetry.proto.resource.v1.Resource.getDefaultInstance(),
            io.opentelemetry.proto.common.v1.InstrumentationScope.getDefaultInstance());

        assertSame(sum, adapter.getUpdated().getSum());
    }

    @Test
    void shouldReturnSameContainedHistogramProtoInstance() {
        io.opentelemetry.proto.metrics.v1.Histogram histogram = io.opentelemetry.proto.metrics.v1.Histogram.getDefaultInstance();
        io.opentelemetry.proto.metrics.v1.Metric metric = io.opentelemetry.proto.metrics.v1.Metric.newBuilder()
            .setName("latency").setHistogram(histogram).build();
        MetricAdapter adapter = new MetricAdapter(metric,
            io.opentelemetry.proto.resource.v1.Resource.getDefaultInstance(),
            io.opentelemetry.proto.common.v1.InstrumentationScope.getDefaultInstance());

        assertSame(histogram, adapter.getUpdated().getHistogram());
    }

    @Test
    void shouldReturnSameContainedExponentialHistogramProtoInstance() {
        io.opentelemetry.proto.metrics.v1.ExponentialHistogram expHist = io.opentelemetry.proto.metrics.v1.ExponentialHistogram.getDefaultInstance();
        io.opentelemetry.proto.metrics.v1.Metric metric = io.opentelemetry.proto.metrics.v1.Metric.newBuilder()
            .setName("latency").setExponentialHistogram(expHist).build();
        MetricAdapter adapter = new MetricAdapter(metric,
            io.opentelemetry.proto.resource.v1.Resource.getDefaultInstance(),
            io.opentelemetry.proto.common.v1.InstrumentationScope.getDefaultInstance());

        assertSame(expHist, adapter.getUpdated().getExponentialHistogram());
    }

    @Test
    void shouldReturnSameContainedSummaryProtoInstance() {
        io.opentelemetry.proto.metrics.v1.Summary summary = io.opentelemetry.proto.metrics.v1.Summary.getDefaultInstance();
        io.opentelemetry.proto.metrics.v1.Metric metric = io.opentelemetry.proto.metrics.v1.Metric.newBuilder()
            .setName("quantiles").setSummary(summary).build();
        MetricAdapter adapter = new MetricAdapter(metric,
            io.opentelemetry.proto.resource.v1.Resource.getDefaultInstance(),
            io.opentelemetry.proto.common.v1.InstrumentationScope.getDefaultInstance());

        assertSame(summary, adapter.getUpdated().getSummary());
    }

    @Test
    void shouldMutateThroughBuilderAfterStateChange() {
        io.opentelemetry.proto.metrics.v1.Metric metric = io.opentelemetry.proto.metrics.v1.Metric.newBuilder()
            .setSum(io.opentelemetry.proto.metrics.v1.Sum.getDefaultInstance())
            .build();

        MetricAdapter adapter = new MetricAdapter(
            metric,
            io.opentelemetry.proto.resource.v1.Resource.getDefaultInstance(),
            io.opentelemetry.proto.common.v1.InstrumentationScope.getDefaultInstance());

        adapter.setDescription("desc");
        adapter.getSum().setMonotonic(true).setAggregationTemporality(AggregationTemporality.DELTA);

        io.opentelemetry.proto.metrics.v1.Metric updated = adapter.getUpdated();
        assertNotSame(metric, updated);
        assertEquals("desc", updated.getDescription());
        assertTrue(updated.getSum().getIsMonotonic());
        assertEquals(io.opentelemetry.proto.metrics.v1.AggregationTemporality.AGGREGATION_TEMPORALITY_DELTA,
            updated.getSum().getAggregationTemporality());
    }

    @Test
    void shouldRejectNullMetric() {
        assertThrows(NullPointerException.class, () -> new MetricAdapter(
            null,
            io.opentelemetry.proto.resource.v1.Resource.getDefaultInstance(),
            io.opentelemetry.proto.common.v1.InstrumentationScope.getDefaultInstance()));
    }
}
