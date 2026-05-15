package io.github.pointertrace.siglet.impl.adapter.metric;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SummaryAdapterTest {
    @Test
    void shouldRejectNullSummary() {
        assertThrows(NullPointerException.class, () -> new SummaryAdapter(null, null));
    }

    @Test
    void shouldKeepSameProtoInstanceWhenUnchanged() {
        io.opentelemetry.proto.metrics.v1.Summary summary = io.opentelemetry.proto.metrics.v1.Summary.getDefaultInstance();
        SummaryAdapter adapter = new SummaryAdapter(summary, null);
        assertSame(summary, adapter.getUpdated());
    }

    @Test
    void shouldReturnSameContainedListInstances() {
        io.opentelemetry.proto.metrics.v1.Summary summary = io.opentelemetry.proto.metrics.v1.Summary.newBuilder()
            .addDataPoints(io.opentelemetry.proto.metrics.v1.SummaryDataPoint.getDefaultInstance())
            .build();
        List<io.opentelemetry.proto.metrics.v1.SummaryDataPoint> dataPoints = summary.getDataPointsList();

        SummaryAdapter adapter = new SummaryAdapter(summary, null);

        assertSame(summary, adapter.getUpdated());
        assertSame(dataPoints, adapter.getUpdated().getDataPointsList());
    }
}
