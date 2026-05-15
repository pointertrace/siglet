package io.github.pointertrace.siglet.impl.adapter.metric;

import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class NumberDataPointAdapterTest {

    @Test
    void shouldSetAndGetNumericValues() {
        io.opentelemetry.proto.metrics.v1.NumberDataPoint point = io.opentelemetry.proto.metrics.v1.NumberDataPoint.getDefaultInstance();
        NumberDataPointAdapter adapter = new NumberDataPointAdapter(point, null);

        assertSame(point, adapter.getUpdated());
        adapter.setAsLong(42L).setFlags(7);
        io.opentelemetry.proto.metrics.v1.NumberDataPoint updated = adapter.getUpdated();

        assertEquals(42L, updated.getAsInt());
        assertEquals(7, updated.getFlags());
    }

    @Test
    void shouldReturnSameContainedListInstances() {
        io.opentelemetry.proto.metrics.v1.NumberDataPoint point = io.opentelemetry.proto.metrics.v1.NumberDataPoint.newBuilder()
            .addAttributes(KeyValue.newBuilder().setKey("k").setValue(AnyValue.newBuilder().setStringValue("v").build()).build())
            .addExemplars(io.opentelemetry.proto.metrics.v1.Exemplar.getDefaultInstance())
            .build();
        List<KeyValue> attributes = point.getAttributesList();
        List<io.opentelemetry.proto.metrics.v1.Exemplar> exemplars = point.getExemplarsList();

        NumberDataPointAdapter adapter = new NumberDataPointAdapter(point, null);

        assertSame(point, adapter.getUpdated());
        assertSame(attributes, adapter.getUpdated().getAttributesList());
        assertSame(exemplars, adapter.getUpdated().getExemplarsList());
    }
}
