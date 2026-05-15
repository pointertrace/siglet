package io.github.pointertrace.siglet.impl.adapter.trace;

import io.github.pointertrace.siglet.api.signal.trace.StatusCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusAdapterTest {

    @Test
    void shouldKeepSameProtoInstanceWhenUnchanged() {
        io.opentelemetry.proto.trace.v1.Status status = io.opentelemetry.proto.trace.v1.Status.getDefaultInstance();
        StatusAdapter adapter = new StatusAdapter(status);
        assertSame(status, adapter.getUpdated());
    }

    @Test
    void shouldMutateAndMapEnum() {
        StatusAdapter adapter = new StatusAdapter(io.opentelemetry.proto.trace.v1.Status.getDefaultInstance());
        adapter.setCode(StatusCode.ERROR).setStatusMessage("failed");

        io.opentelemetry.proto.trace.v1.Status updated = adapter.getUpdated();
        assertEquals(io.opentelemetry.proto.trace.v1.Status.StatusCode.STATUS_CODE_ERROR, updated.getCode());
        assertEquals("failed", updated.getMessage());
    }

    @Test
    void shouldRejectNullStatus() {
        assertThrows(NullPointerException.class, () -> new StatusAdapter(null));
    }
}
