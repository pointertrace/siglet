package io.github.pointertrace.siglet.impl.adapter;

import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InstrumentationScopeAdapterTest {
    @Test
    void shouldRejectNullScope() {
        assertThrows(NullPointerException.class, () -> new InstrumentationScopeAdapter(null));
    }

    @Test
    void shouldKeepSameProtoInstanceWhenUnchanged() {
        io.opentelemetry.proto.common.v1.InstrumentationScope scope = io.opentelemetry.proto.common.v1.InstrumentationScope.getDefaultInstance();
        InstrumentationScopeAdapter adapter = new InstrumentationScopeAdapter(scope);
        assertSame(scope, adapter.getUpdated());
    }

    @Test
    void shouldReturnSameContainedListInstances() {
        io.opentelemetry.proto.common.v1.InstrumentationScope scope = io.opentelemetry.proto.common.v1.InstrumentationScope.newBuilder()
            .addAttributes(KeyValue.newBuilder().setKey("k").setValue(AnyValue.newBuilder().setStringValue("v").build()).build())
            .build();
        List<KeyValue> attributes = scope.getAttributesList();

        InstrumentationScopeAdapter adapter = new InstrumentationScopeAdapter(scope);

        assertSame(scope, adapter.getUpdated());
        assertSame(attributes, adapter.getUpdated().getAttributesList());
    }
}
