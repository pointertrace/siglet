package io.github.pointertrace.siglet.impl.adapter.metric;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SumAdapterTest {
    @Test
    void shouldRejectNullSum() {
        assertThrows(NullPointerException.class, () -> new SumAdapter(null, null));
    }

    @Test
    void shouldKeepSameProtoInstanceWhenUnchanged() {
        io.opentelemetry.proto.metrics.v1.Sum sum = io.opentelemetry.proto.metrics.v1.Sum.getDefaultInstance();
        SumAdapter adapter = new SumAdapter(sum, null);
        assertSame(sum, adapter.getUpdated());
    }

    @Test
    void shouldReturnSameContainedListInstances() {
        io.opentelemetry.proto.metrics.v1.Sum sum = io.opentelemetry.proto.metrics.v1.Sum.newBuilder()
            .addDataPoints(io.opentelemetry.proto.metrics.v1.NumberDataPoint.getDefaultInstance())
            .build();
        List<io.opentelemetry.proto.metrics.v1.NumberDataPoint> dataPoints = sum.getDataPointsList();

        SumAdapter adapter = new SumAdapter(sum, null);

        assertSame(sum, adapter.getUpdated());
        assertSame(dataPoints, adapter.getUpdated().getDataPointsList());
    }
}
