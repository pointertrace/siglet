package io.github.pointertrace.siglet.impl.adapter.metric;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExponentialHistogramAdapterTest {
    @Test
    void shouldRejectNullExponentialHistogram() {
        assertThrows(NullPointerException.class, () -> new ExponentialHistogramAdapter(null, null));
    }

    @Test
    void shouldKeepSameProtoInstanceWhenUnchanged() {
        io.opentelemetry.proto.metrics.v1.ExponentialHistogram expHist =
            io.opentelemetry.proto.metrics.v1.ExponentialHistogram.getDefaultInstance();
        ExponentialHistogramAdapter adapter = new ExponentialHistogramAdapter(expHist, null);
        assertSame(expHist, adapter.getUpdated());
    }

    @Test
    void shouldReturnSameContainedListInstances() {
        io.opentelemetry.proto.metrics.v1.ExponentialHistogram expHist =
            io.opentelemetry.proto.metrics.v1.ExponentialHistogram.newBuilder()
                .addDataPoints(io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.getDefaultInstance())
                .build();
        List<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint> dataPoints = expHist.getDataPointsList();

        ExponentialHistogramAdapter adapter = new ExponentialHistogramAdapter(expHist, null);

        assertSame(expHist, adapter.getUpdated());
        assertSame(dataPoints, adapter.getUpdated().getDataPointsList());
    }
}
