package io.github.pointertrace.siglet.impl.adapter.trace;

import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

class EventAdapterTest {

    @Test
    void shouldKeepSameProtoInstanceWhenUnchanged() {
        io.opentelemetry.proto.trace.v1.Span.Event event = io.opentelemetry.proto.trace.v1.Span.Event.getDefaultInstance();
        EventAdapter adapter = new EventAdapter(event, null);
        assertSame(event, adapter.getUpdated());
    }

    @Test
    void shouldReturnSameContainedListInstances() {
        io.opentelemetry.proto.trace.v1.Span.Event event = io.opentelemetry.proto.trace.v1.Span.Event.newBuilder()
            .setName("evt")
            .addAttributes(KeyValue.newBuilder().setKey("k").setValue(AnyValue.newBuilder().setStringValue("v").build()).build())
            .build();
        List<KeyValue> attributes = event.getAttributesList();

        EventAdapter adapter = new EventAdapter(event, null);

        assertSame(attributes, adapter.getUpdated().getAttributesList());
    }
}
