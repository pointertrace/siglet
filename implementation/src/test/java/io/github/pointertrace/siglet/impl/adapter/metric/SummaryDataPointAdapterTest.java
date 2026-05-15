package io.github.pointertrace.siglet.impl.adapter.metric;

import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SummaryDataPointAdapterTest {
    @Test
    void shouldUpdateSummaryValues() {
        io.opentelemetry.proto.metrics.v1.SummaryDataPoint value = io.opentelemetry.proto.metrics.v1.SummaryDataPoint.getDefaultInstance();
        SummaryDataPointAdapter adapter = new SummaryDataPointAdapter(value, null);
        assertSame(value, adapter.getUpdated());
        adapter.setCount(2).setSum(10.0);
        io.opentelemetry.proto.metrics.v1.SummaryDataPoint updated = adapter.getUpdated();
        assertEquals(2L, updated.getCount());
        assertEquals(10.0, updated.getSum());
    }

    @Test
    void shouldReturnSameContainedListInstances() {
        io.opentelemetry.proto.metrics.v1.SummaryDataPoint value = io.opentelemetry.proto.metrics.v1.SummaryDataPoint.newBuilder()
            .addAttributes(KeyValue.newBuilder().setKey("k").setValue(AnyValue.newBuilder().setStringValue("v").build()).build())
            .addQuantileValues(io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile.newBuilder()
                .setQuantile(0.5).setValue(100.0).build())
            .build();
        List<KeyValue> attributes = value.getAttributesList();
        List<io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile> quantileValues = value.getQuantileValuesList();

        SummaryDataPointAdapter adapter = new SummaryDataPointAdapter(value, null);

        assertSame(value, adapter.getUpdated());
        assertSame(attributes, adapter.getUpdated().getAttributesList());
        assertSame(quantileValues, adapter.getUpdated().getQuantileValuesList());
    }

    @Test
    void shouldRejectNullDataPoint() {
        assertThrows(NullPointerException.class, () -> new SummaryDataPointAdapter(null, null));
    }
}
