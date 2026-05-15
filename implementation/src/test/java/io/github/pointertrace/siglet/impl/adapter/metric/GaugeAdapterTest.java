package io.github.pointertrace.siglet.impl.adapter.metric;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GaugeAdapterTest {
    @Test
    void shouldRejectNullGauge() {
        assertThrows(NullPointerException.class, () -> new GaugeAdapter(null, null));
    }
    @Test
    void shouldKeepSameProtoInstanceWhenUnchanged() {
        io.opentelemetry.proto.metrics.v1.Gauge gauge = io.opentelemetry.proto.metrics.v1.Gauge.getDefaultInstance();
        GaugeAdapter adapter = new GaugeAdapter(gauge, null);
        assertSame(gauge, adapter.getUpdated());
    }
    @Test
    void shouldReturnSameContainedListInstances() {
        io.opentelemetry.proto.metrics.v1.Gauge gauge = io.opentelemetry.proto.metrics.v1.Gauge.newBuilder()
            .addDataPoints(io.opentelemetry.proto.metrics.v1.NumberDataPoint.getDefaultInstance())
            .build();
        List<io.opentelemetry.proto.metrics.v1.NumberDataPoint> dataPoints = gauge.getDataPointsList();

        GaugeAdapter adapter = new GaugeAdapter(gauge, null);

        assertSame(gauge, adapter.getUpdated());
        assertSame(dataPoints, adapter.getUpdated().getDataPointsList());
    }
    @Test
    void shouldReturnDataPoints() {
        io.opentelemetry.proto.metrics.v1.Gauge gauge = io.opentelemetry.proto.metrics.v1.Gauge.newBuilder()
            .addDataPoints(io.opentelemetry.proto.metrics.v1.NumberDataPoint.getDefaultInstance())
            .build();
        GaugeAdapter adapter = new GaugeAdapter(gauge, null);
        assertNotNull(adapter.getDataPoints());
        assertEquals(1, adapter.getDataPoints().getSize());
    }
}
