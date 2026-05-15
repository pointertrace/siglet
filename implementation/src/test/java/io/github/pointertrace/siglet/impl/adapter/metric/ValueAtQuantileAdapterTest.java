package io.github.pointertrace.siglet.impl.adapter.metric;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValueAtQuantileAdapterTest {
    @Test
    void shouldRejectNullValueAtQuantile() {
        assertThrows(NullPointerException.class, () -> new ValueAtQuantileAdapter(null, null));
    }
}
