package io.github.pointertrace.siglet.impl.adapter.metric;

import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExemplarAdapterTest {
    @Test
    void shouldRejectNullExemplar() {
        assertThrows(NullPointerException.class, () -> new ExemplarAdapter(null, null));
    }

    @Test
    void shouldKeepSameProtoInstanceWhenUnchanged() {
        io.opentelemetry.proto.metrics.v1.Exemplar exemplar = io.opentelemetry.proto.metrics.v1.Exemplar.getDefaultInstance();
        ExemplarAdapter adapter = new ExemplarAdapter(exemplar, null);
        assertSame(exemplar, adapter.getUpdated());
    }

    @Test
    void shouldReturnSameContainedListInstances() {
        io.opentelemetry.proto.metrics.v1.Exemplar exemplar = io.opentelemetry.proto.metrics.v1.Exemplar.newBuilder()
            .addFilteredAttributes(KeyValue.newBuilder().setKey("k").setValue(AnyValue.newBuilder().setStringValue("v").build()).build())
            .build();
        List<KeyValue> filteredAttributes = exemplar.getFilteredAttributesList();

        ExemplarAdapter adapter = new ExemplarAdapter(exemplar, null);

        assertSame(filteredAttributes, adapter.getUpdated().getFilteredAttributesList());
    }
}
