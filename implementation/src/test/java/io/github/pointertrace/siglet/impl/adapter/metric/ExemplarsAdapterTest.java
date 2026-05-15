package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.Exemplar;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

class ExemplarsAdapterTest {
    @Test
    void shouldRejectNullExemplars() {
        assertThrows(NullPointerException.class, () -> new ExemplarsAdapter(null));
    }
    @Test
    void shouldReturnCorrectSize() {
        io.opentelemetry.proto.metrics.v1.Exemplar e = io.opentelemetry.proto.metrics.v1.Exemplar.getDefaultInstance();
        ExemplarsAdapter adapter = new ExemplarsAdapter(Collections.singletonList(e));
        assertEquals(1, adapter.getSize());
    }
    @Test
    void shouldGetExemplarAtIndex() {
        io.opentelemetry.proto.metrics.v1.Exemplar e = io.opentelemetry.proto.metrics.v1.Exemplar.getDefaultInstance();
        ExemplarsAdapter adapter = new ExemplarsAdapter(Collections.singletonList(e));
        Exemplar exemplar = adapter.get(0);
        assertNotNull(exemplar);
    }
    @Test
    void shouldRemoveExemplarAtIndex() {
        io.opentelemetry.proto.metrics.v1.Exemplar e = io.opentelemetry.proto.metrics.v1.Exemplar.getDefaultInstance();
        AtomicReference<List<io.opentelemetry.proto.metrics.v1.Exemplar>> captured = new AtomicReference<>();
        ExemplarsAdapter adapter = new ExemplarsAdapter(Collections.singletonList(e), captured::set);
        adapter.remove(0);
        assertEquals(0, adapter.getSize());
        assertNotNull(captured.get());
    }
}
