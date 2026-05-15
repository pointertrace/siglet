package io.github.pointertrace.siglet.impl.adapter.metric;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HistogramAdapterTest {
    @Test
    void shouldRejectNullHistogram() {
        assertThrows(NullPointerException.class, () -> new HistogramAdapter(null, null));
    }

    @Test
    void shouldKeepSameProtoInstanceWhenUnchanged() {
        io.opentelemetry.proto.metrics.v1.Histogram histogram = io.opentelemetry.proto.metrics.v1.Histogram.getDefaultInstance();
        HistogramAdapter adapter = new HistogramAdapter(histogram, null);
        assertSame(histogram, adapter.getUpdated());
    }

    @Test
    void shouldReturnSameContainedListInstances() {
        io.opentelemetry.proto.metrics.v1.Histogram histogram = io.opentelemetry.proto.metrics.v1.Histogram.newBuilder()
            .addDataPoints(io.opentelemetry.proto.metrics.v1.HistogramDataPoint.getDefaultInstance())
            .build();
        List<io.opentelemetry.proto.metrics.v1.HistogramDataPoint> dataPoints = histogram.getDataPointsList();

        HistogramAdapter adapter = new HistogramAdapter(histogram, null);

        assertSame(histogram, adapter.getUpdated());
        assertSame(dataPoints, adapter.getUpdated().getDataPointsList());
    }
}
